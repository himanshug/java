package io.kubernetes.client.examples;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.apis.ExtensionsV1beta1Api;
import io.kubernetes.client.models.ExtensionsV1beta1Deployment;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.util.ClientBuilder;

import java.io.IOException;
import java.util.ArrayList;

/*
kubectl get po --template "{{.metadata.annotations}}" zk-0
 */
public class DruidPodPatch
{
  public static void main(String[] args) throws IOException, ApiException
  {
    String jsonPatchStr =
        "{\"op\": \"add\", \"path\": \"/metadata/annotations/druidNodeInfo\", \"value\":\"randomstuff\"}";
    String labelPatchStr =
        "{\"op\": \"remove\", \"path\": \"/metadata/labels/druidDiscoveryAnnouncement\", \"value\":\"done\"}";

    ApiClient client = ClientBuilder.defaultClient();
    Configuration.setDefaultApiClient(client);

    ArrayList<JsonObject> arr = new ArrayList<>();
    arr.add(((JsonElement) deserialize(jsonPatchStr, JsonElement.class)).getAsJsonObject());
    arr.add(((JsonElement) deserialize(labelPatchStr, JsonElement.class)).getAsJsonObject());

    CoreV1Api api = new CoreV1Api();
    try {
      V1Pod patchedPod = api.patchNamespacedPod("druid-cbs-druid-brokers-0", "cja-dev", arr, "true", null);
      System.out.println(patchedPod.getMetadata().getAnnotations());
    } catch (ApiException ex) {
      System.out.println(ex.getResponseBody());
    }
  }

  private static Object deserialize(String jsonStr, Class<?> targetClass) {
    Object obj = (new Gson()).fromJson(jsonStr, targetClass);
    return obj;
  }
}
