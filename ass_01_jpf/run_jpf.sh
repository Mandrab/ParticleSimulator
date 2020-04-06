#!/bin/bash

java -jar jpf-core-JPF-8.0/build/RunJPF.jar +classpath=bin/ main.Controller -bodies 1000 -steps 1000
