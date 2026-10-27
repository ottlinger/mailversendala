/*
 * Copyright (c) P. Ottlinger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.aikiit.mailversendala.template;

import de.aikiit.mailversendala.MailConfig;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Validates and snapshots local mail resources once per run. No network resolver is used.
 * Files are kept in memory so later filesystem changes cannot change a validated message.
 */
public final class MailResources {
    private final Path directory;
    private final Map<String, Asset> references = new HashMap<>();
    private final Map<Path, Asset> files = new HashMap<>();
    private final List<Asset> attachments = new ArrayList<>();

    /**
     * Load configured, comma-separated attachments relative to the real template directory.
     * @param configuration template directory and attachment references.
     * @throws IOException if a configured file is inaccessible or outside the directory.
     */
    public MailResources(MailConfig configuration) throws IOException {
        directory = Path.of(configuration.getTemplatePath()).toRealPath();
        if (!Files.isDirectory(directory)) {
            throw new IOException("Template directory is not a directory: " + directory);
        }
        Map<Path, Asset> unique = new LinkedHashMap<>();
        for (String reference : Objects.requireNonNullElse(configuration.getAttachments(), "").split(",")) {
            if (!reference.isBlank()) {
                Asset asset = load(reference.trim());
                unique.putIfAbsent(asset.path(), asset);
            }
        }
        attachments.addAll(unique.values());
    }

    /**
     * Validate every inline image in a rendered message before the first delivery.
     * @param template rendered message bodies.
     * @throws IOException for missing, external, unsupported or escaping image references.
     */
    public void validate(MailTemplate template) throws IOException {
        Document document = parse(template.getHtml());
        if (!document.select("[srcset]").isEmpty()) {
            throw new IOException("srcset is not supported; use local img src references");
        }
        for (Element image : document.select("img")) {
            Asset asset = load(image.attr("src"));
            if (!asset.contentType().startsWith("image/")) {
                throw new IOException("Inline resource is not an image: " + image.attr("src"));
            }
        }
    }

    /**
     * Embed local images using Content-IDs and add ordinary downloadable attachments.
     * @param email configured outgoing message.
     * @param template rendered HTML and plain-text alternatives.
     * @throws EmailException if resource validation or MIME construction fails.
     */
    public void applyTo(HtmlEmail email, MailTemplate template) throws EmailException {
        try {
            validate(template);
            Document document = parse(template.getHtml());
            Map<Path, String> contentIds = new HashMap<>();
            for (Element image : document.select("img")) {
                Asset asset = load(image.attr("src"));
                String cid = contentIds.get(asset.path());
                if (cid == null) {
                    cid = email.embed(asset.dataSource(), directory.relativize(asset.path()).toString());
                    contentIds.put(asset.path(), cid);
                }
                image.attr("src", "cid:" + cid);
            }
            email.setHtmlMsg(contentIds.isEmpty() ? template.getHtml() : document.outerHtml());
            email.setTextMsg(template.getPlaintext());
            for (Asset asset : attachments) {
                email.attach(asset.dataSource(), asset.filename(), "", EmailAttachment.ATTACHMENT);
            }
        } catch (IOException e) {
            throw new EmailException("Cannot prepare local mail resources", e);
        }
    }

    private Document parse(String html) {
        Document document = Jsoup.parse(html);
        document.outputSettings().prettyPrint(false);
        return document;
    }

    private Asset load(String reference) throws IOException {
        Asset cached = references.get(reference);
        if (cached != null) {
            return cached;
        }
        Path relative;
        try {
            URI uri = new URI(reference.replace(" ", "%20"));
            if (reference.isBlank() || reference.contains("\\") || uri.isAbsolute()
                    || uri.getRawAuthority() != null || uri.getRawQuery() != null || uri.getRawFragment() != null) {
                throw new IOException("Only relative local resource paths are allowed: " + reference);
            }
            String path = uri.getPath();
            if (path == null) {
                throw new IOException("Resource path is missing: " + reference);
            }
            relative = Path.of(path);
        } catch (URISyntaxException | IllegalArgumentException e) {
            throw new IOException("Invalid local resource path: " + reference, e);
        }
        if (relative.isAbsolute()) {
            throw new IOException("Absolute resource paths are not allowed: " + reference);
        }
        Path file = directory.resolve(relative).toRealPath();
        if (!file.startsWith(directory) || !Files.isRegularFile(file) || !Files.isReadable(file)) {
            throw new IOException("Resource must be a readable file inside templatepath: " + reference);
        }
        Asset asset = files.get(file);
        if (asset == null) {
            Path filename = file.getFileName();
            if (filename == null) {
                throw new IOException("Resource filename is missing: " + reference);
            }
            String contentType = URLConnection.guessContentTypeFromName(filename.toString());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            asset = new Asset(file, filename.toString(), Files.readAllBytes(file), contentType);
            files.put(file, asset);
        }
        references.put(reference, asset);
        return asset;
    }

    private record Asset(Path path, String filename, byte[] bytes, String contentType) {
        ByteArrayDataSource dataSource() {
            ByteArrayDataSource source = new ByteArrayDataSource(bytes, contentType);
            source.setName(filename);
            return source;
        }
    }
}
