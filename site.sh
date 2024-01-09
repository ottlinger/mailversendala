#!/bin/bash
echo "Generating new mvn site ..."
./mvnw clean install org.pitest:pitest-maven:mutationCoverage site:site 
echo "DONE - ready to commit and push"
cp -rf target/site/* docs
git add -f docs/
git commit -a -m "Issue #304: Update site via script"
git push
