package io.kubernetes.client.examples;

import com.google.gson.reflect.TypeToken;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Watch;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/** A simple example of how to use Watch API to watch changes in Namespace list. */
public class DruidPodWatch {
  public static void main(String[] args) throws IOException, ApiException
  {
    ApiClient client = Config.defaultClient();
    client.getHttpClient().setReadTimeout(60, TimeUnit.SECONDS);
    Configuration.setDefaultApiClient(client);

    CoreV1Api api = new CoreV1Api();

    Watch<V1Pod> watch =
        Watch.createWatch(
            client,
            api.listNamespacedPodCall("cja-dev", false, null, null,
                                      null, "druid_cr=cbs-druid,druidDiscoveryAnnouncement=done", null, null, null,
                                      true, null, null),
//            api.listNamespaceCall(
//                null, null, null, null, null, 5, null, null, Boolean.TRUE, null, null),
            new TypeToken<Watch.Response<V1Pod>>() {}.getType());

    try {
      for (Watch.Response<V1Pod> item : watch) {
        System.out.println(String.format("Item is[%s][%s][%s][%s]", item.type, item.status, item.object.getMetadata().getName(), item.object.getMetadata().getAnnotations().get("druidNodeInfo")));
//        System.out.printf("%s : %s%n", item.type, item.object.getMetadata().getName());
      }
    } finally {
      watch.close();
    }
  }
}
