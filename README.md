# Quick

**Quick** is a Java utility library designed to make everyday Java code shorter, clearer, and easier to read.

Instead of writing scattered boilerplate across `System.out`, `String`, collections, loops, files, and helper logic, Quick gives developers a unified and lightweight API focused on speed, readability, and developer productivity.

```java
Quick.print("Hello from Quick");
```

***

## Why Quick?

Java is powerful, but many common tasks still feel verbose.
Quick reduces that friction by offering simple wrappers and fluent utilities for the things developers do all the time.

### What Quick tries to solve

- Too much boilerplate for simple tasks.
- Different APIs for similar daily operations.
- Repetitive code for strings, loops, maps, sets, files, and lists.
- Less readable code when doing small utility work.

### What makes Quick different

- One simple style across many utilities.
- Fluent and readable method names.
- Faster day-to-day coding for common tasks.
- Easier onboarding for beginners.
- Cleaner demo code, tutorials, and internal tools.

***

## Core idea

Quick is built around one goal:

> **Write less Java for common tasks.**

It is not trying to replace Java.
It is trying to make Java feel lighter for routine work.

***

## Current library status

The current Quick library is already strong as a productivity layer for everyday Java work, especially in utility-heavy code.

### Estimated completion

| Area | Estimated completion |
|------|----------------------|
| Overall library | **68%** |
| API usability and readability | **85%** |
| Core utility concept | **90%** |
| Real-world packaging and publishing readiness | **55%** |
| Documentation maturity | **50%** |
| Ecosystem/distribution readiness | **45%** |

### Strongest parts right now

- `QuickFile`
- `QuickString`
- `QuickList`
- `QuickJson`
- `QuickMap`
- `QuickSet`

### What still improves the project most

- Better publishing flow.
- Better README and examples.
- Stable versioning.
- JitPack / Maven Central distribution.
- More polished docs and use cases.

***

## Main features

Quick is designed as a growing toolbox around common Java work.

### Printing

```java
Quick.print("Hello");
```

Instead of:

```java
System.out.println("Hello");
```

### Strings

```java
Quick.print(new QuickString("  hello world  ").trim().upper());
```

### Numbers

```java
Quick.print(new QuickNumber(15.678).round(2));
```

### Lists

```java
Quick.print(new QuickList<>(1, 2, 3, 4).filter(n -> n % 2 == 0));
```

### Maps

```java
Quick.print(new QuickMap<String, Integer>()
        .put("Ali", 22)
        .put("Sara", 20)
        .keys());
```

### Sets

```java
Quick.print(new QuickSet<>("java", "quick", "java").union(new QuickSet<>("api")));
```

### Loops

```java
Quick.print(new QuickLoop(1, 11).filter(i -> i % 2 == 0));
```

### Files

```java
Quick.file("notes.txt").write("Hello").append("\nQuick");
```

***

## Why it feels better than plain Java

Quick focuses on **intent**.

Instead of making the developer think in terms of low-level utility steps every time, it lets them express the action directly.

### Example: printing

**Quick**
```java
Quick.print("Hello");
```

**Java**
```java
System.out.println("Hello");
```

### Example: string extraction

**Quick**
```java
QuickString text = new QuickString("Name: Ali | Age: 22");
Quick.print(text.between("Name:", "|").trim());
```

**Java**
```java
String text = "Name: Ali | Age: 22";
Quick.print(text.substring(text.indexOf("Name:") + 5, text.indexOf("|")).trim());
```

### Example: loops

**Quick**
```java
Quick.print(new QuickLoop(1, 11).filter(i -> i % 2 == 0));
```

**Java**
```java
Quick.print(java.util.stream.IntStream.range(1, 11)
        .filter(i -> i % 2 == 0)
        .boxed()
        .toList());
```

***

## Who is Quick for?

Quick is especially useful for:

- Developers who want less boilerplate.
- Beginners learning Java who need cleaner examples.
- Utility-heavy backend code.
- Prototyping and internal tools.
- Educational code and demos.
- Teams that prefer readability over ceremony.

***

## Installation

Quick can be used through **JitPack** after publishing a tagged release.

### Step 1: Add the JitPack repository

#### Gradle (`settings.gradle`)

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2: Add the dependency

```gradle
dependencies {
    implementation 'com.github.nuseirat:quick:Tag'
}
```

Replace `Tag` with your real release tag, for example:

```gradle
dependencies {
    implementation 'com.github.nuseirat:quick:v1.0.0'
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
    <groupId>com.github.nuseirat</groupId>
    <artifactId>quick</artifactId>
    <version>v1.0.0</version>
</dependency>
```

***

## How to publish a release

### 1. Commit and push your latest code

```bash
git add .
git commit -m "prepare release"
git push origin main
```

### 2. Create a tag

```bash
git tag -a v1.0.0 -m "First release"
git push origin v1.0.0
```

### 3. Create a GitHub release

On GitHub:

- Open your repository.
- Go to **Releases**.
- Click **Draft a new release**.
- Select tag `v1.0.0`.
- Add a title like `Quick v1.0.0`.
- Add release notes.
- Click **Publish release**.

### 4. Open JitPack

Go to [JitPack](https://jitpack.io/).

Then:

- Search for `nuseirat/quick`
- Build the project
- Copy the generated dependency
- Paste it into your README

***

## Recommended release notes

Example:

```text
Quick v1.0.0

First public release of Quick.
Includes utility APIs for printing, strings, lists, sets, maps, loops, and files.
Focused on reducing Java boilerplate and improving readability.
```

***

## Example usage

```java
import com.quick.Quick;
import com.quick.string.QuickString;

public class Main {
    public static void main(String[] args) {
        Quick.print("Hello from Quick");
        Quick.print(new QuickString("  hello quick  ").trim().upper());
    }
}
```

***

## Design philosophy

Quick follows a few simple rules:

- Keep common tasks short.
- Prefer readable names.
- Reduce repeated boilerplate.
- Make utility code feel consistent.
- Help Java stay practical for daily work.

***

## Roadmap

Planned improvements include:

- More examples for every module.
- Better docs for each class.
- Stronger publishing workflow.
- Maven Central support.
- Better test coverage.
- More stable semantic versioning.
- A richer top-level `Quick` API.

***

## Share this release

If you want to share Quick with others, send:

- The GitHub repository link
- The JitPack installation snippet
- A short before/after example

Example:

```java
// Quick
Quick.print(new QuickString("  hello world  ").trim().upper());

// Plain Java
System.out.println("  hello world  ".trim().toUpperCase());
```

***

## Summary

Quick is a Java utility library that aims to make common code shorter, cleaner, and more expressive.
It focuses on practical productivity, fluent APIs, and a smoother developer experience for everyday Java work.

If Java feels too verbose for routine tasks, Quick exists to reduce that friction.
