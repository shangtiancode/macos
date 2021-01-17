#!/bin/bash
mvn -U clean package -Dmaven.test.skip=true
cp ./macos-broker/target/macos-broker-0.0.1.release.jar ./