package io.spotback.util;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.logs.*;
import com.amazonaws.services.logs.model.*;
import io.vertx.core.json.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class LoggingUtil {
  private static AWSLogsAsyncClientBuilder client;

  private class AsyncLogHandler implements AsyncHandler<PutLogEventsRequest, PutLogEventsResult> {
    @Override
    public void onError(Exception e) {
      System.out.println(e.getMessage());
      System.exit(1);
    }

    @Override
    public void onSuccess(PutLogEventsRequest request, PutLogEventsResult putLogEventsResult) {

    }

  }

  private static void setClient() {
    if(client == null) {
      client = AWSLogsAsyncClientBuilder.standard().withCredentials(new DefaultAWSCredentialsProviderChain());
      client.setRegion("us-east-2");
    }
  }

  public static List<String> setSequenceTokens() {
    setClient();
    AWSLogsAsync log = client.build();
    Future<DescribeLogStreamsResult> fut = log.describeLogStreamsAsync(new DescribeLogStreamsRequest("spotback/user-service"));
    List<String> seqTokens = null;
      try {
        seqTokens = fut.get().getLogStreams().stream().map(LogStream::getUploadSequenceToken).collect(Collectors.toList());;
      } catch (Exception e) {
        e.printStackTrace();
      }
    log.shutdown();
    log = null;
    return seqTokens;
  }


  public String logEvent(JsonObject logs, String className, String sequenceToken) {
    logs.put("sequence.token", sequenceToken);
    InputLogEvent event = new InputLogEvent().withMessage(logs.toString()).withTimestamp(System.currentTimeMillis());
    List<InputLogEvent> list = Arrays.asList(event);
    PutLogEventsRequest request = new PutLogEventsRequest("spotback/user-service", className, list).withSequenceToken(sequenceToken);
    AWSLogsAsync logAsync = client.build();
    Future<PutLogEventsResult> fut = logAsync.putLogEventsAsync(request, new AsyncLogHandler());
    String token = null;
      try {
        token = fut.get().getNextSequenceToken();
      } catch (Exception e) {
        e.printStackTrace();
      }
      logAsync.shutdown();
    return token;
  }
}
