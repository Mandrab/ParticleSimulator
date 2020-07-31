#!/bin/bash

java -jar lib/jpf-core-JPF-8.0/build/RunJPF.jar +classpath=bin/ jpf.BodiesSimulator $1
