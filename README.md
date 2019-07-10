# Result

[![Build Status](https://travis-ci.org/ivpal/result.svg?branch=master)](https://travis-ci.org/ivpal/result)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/bd46b7067cf24727b10ee2638403cb05)](https://www.codacy.com/app/IvanovPvl/result)
[![codecov](https://codecov.io/gh/ivpal/result/branch/master/graph/badge.svg)](https://codecov.io/gh/ivpal/result)
[![JitPack](https://jitpack.io/v/ivpal/result.svg)](https://jitpack.io/#ivpal/result)

This is a simple library for modelling success/failure of operations in Java. In short, it is a model in type of `Result<V, E extends Throwable>`.

## Ideology

`Result<V, E extends Throwable>` is to provide higher abstraction of operation that can be ended with result either success or failure. `Result.Success` represents `value` in case of success, and `Result.Failure` represents `error` in case of failure which is upper bounded with `Throwable` type.

## Installation

### Gradle

``` Groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.ivpal:result:<latest-version>'
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.ivpal</groupId>
    <artifactId>result</artifactId>
    <version>VERSION</version>
</dependency>
```

## License

Result is released under the [MIT](http://opensource.org/licenses/MIT) license.
