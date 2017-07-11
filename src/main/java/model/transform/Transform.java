package model.transform;

import model.Client;
import model.FileLink;
import model.Security;
import okhttp3.HttpUrl;
import util.FilestackService;
import util.Networking;

import java.util.ArrayList;

/**
 * Base class for file transformations and conversions.
 */
public class Transform {
    String apiKey;
    String source;

    ArrayList<TransformTask> tasks;

    FilestackService.Process processService;

    Transform(Client client, String source) {
        this(client, source, null);
    }

    Transform(FileLink fileLink) {
        this(null, null, fileLink);
    }

    Transform(Client client, String source, FileLink fileLink) {
        if (client != null) {
            this.apiKey = client.getApiKey();
            this.source = source;
        } else {
            this.source = fileLink.getHandle();
        }

        this.tasks = new ArrayList<>();
        this.processService = Networking.getProcessService();

        Security security = client != null ? client.getSecurity() : fileLink.getSecurity();
        if (security != null) {
            TransformTask securityTask = new TransformTask("security");
            securityTask.addOption("policy", security.getPolicy());
            securityTask.addOption("signature", security.getSignature());
            this.tasks.add(securityTask);
        }
    }

    /**
     * Build tasks into single string to insert into request.
     */
    protected String getTasksString() {
        if (tasks.size() == 0)
            return "";

        StringBuilder stringBuilder = new StringBuilder();
        for (TransformTask task : tasks)
            stringBuilder.append(task.toString()).append('/');
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        return stringBuilder.toString();
    }

    public String url() {
        String tasksString = getTasksString();
        HttpUrl httpUrl;

        if (apiKey != null)
            httpUrl = processService.getExternal(apiKey, tasksString, source).request().url();
        else
            httpUrl = processService.get(tasksString, source).request().url();

        // When forming the request we add a / between tasks, then add that entire string as a path variable
        // Because it's added as a single path variable, the / is URL encoded
        // That's a little confusing so we're replacing "%2F" with "/" for a more expected URL
        return httpUrl.toString().replace("%2F", "/");
    }
}
