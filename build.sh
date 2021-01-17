#!/bin/bash
git stash
git pull --rebase;
mvn -U clean package -Dmaven.test.skip=true
cp ./macos-broker/target/macos-broker-0.0.1.release.jar ./
cp ./macos-broker/target/macos-demo-0.0.1.release.jar ./
git stash pop