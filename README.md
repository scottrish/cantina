# Cantina Code Problem

Repo containing my solution to the Cantina coding problem.

## Setup
Java 8

Apache Ant

## Usage
ant build

export CLASSPATH=lib/jackson-annotations-2.11.2.jar:lib/jackson-core-2.11.2.jar:lib/jackson-databind-2.11.2.jar:dist/lib/Cantina.jar

java -cp $CLASSPATH cantina.CLI

The following selectors are supported:

1. Class - simply enter a string with the node class.
2. classNames - prefix the search string with a period, for example, .container
3. Identifier - prefix the search string with a hash, for example, #apply

## Compound Selectors
Not yet implemented

## Chained Selectors
Not yet implemented


