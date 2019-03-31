# logging-util
Spotback logging utility

This utility can be used to log json data to aws cloudwatch assuming you have either environment variable set for aws or the credintials file on your system. To use this tool just get a valid sequence token with `LoggingUtil.setSequenceTokens();` and then construct an instance, and finally use `logEvent(JsonObject log, String className, String sequenceToken)`.

**Example**

```java
public void loggerInit() {
    List<String> list2 = LoggingUtil.setSequenceTokens();
    String sequenceToken = list2.get(4);
    LoggingUtil LOG = new LoggingUtil();
    JsonObject consolidatedLog = new JsonObject();
    consolidatedLog.put("message", "some event happened");
    sequenceToken = LOG.logEvent(consolidatedLog, getClass().getName(), sequenceToken);
}
```

To get a this project into your build:

# Step 1. Add the JitPack repository to your build file
**maven**

	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
# Step 2. Add the dependency
	<dependency>
	    <groupId>com.github.Spotback</groupId>
	    <artifactId>logging-util</artifactId>
	    <version>Tag</version>
	</dependency>
