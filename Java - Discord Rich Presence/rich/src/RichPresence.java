import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;

import java.time.Instant;

import data.SystemTrayRichPresenceEntry;

public class RichPresence {

    private final SystemTrayRichPresenceEntry entry;

    private boolean shouldRun = true;

    public RichPresence(final SystemTrayRichPresenceEntry entry) {
        this.entry = entry;
        new Thread(this::set).start();
    }

    public void stop() {
        this.shouldRun = false;
    }

    private void set() {
        System.out.println("Will set presence for: " + this.entry.menuItemName);

        try(final CreateParams params = new CreateParams())
        {
            params.setClientID(this.entry.clientId);
            params.setFlags(CreateParams.getDefaultFlags());

            try(final Core core = new Core(params))
            {
                // Create the Activity
                try(final Activity activity = new Activity())
                {
                    if (this.entry.details != null) activity.setDetails(this.entry.details);
                    if (this.entry.state != null) activity.setState(this.entry.state);

                    // Setting a start time causes an "elapsed" field to appear
                    activity.timestamps().setStart(Instant.now());

                    // Make a "cool" image show up
                    if (this.entry.largeImage != null) activity.assets().setLargeImage(this.entry.largeImage);
                    if (this.entry.largeText != null) activity.assets().setLargeText(this.entry.largeText);
                    if (this.entry.smallImage != null) activity.assets().setSmallImage(this.entry.smallImage);
                    if (this.entry.smallText != null) activity.assets().setSmallText(this.entry.smallText);

                    // Finally, update the current activity to our activity
                    core.activityManager().updateActivity(activity);
                }

                // Run callbacks forever
                while(this.shouldRun)
                {
                    core.runCallbacks();
                    try
                    {
                        // Sleep a bit to save CPU
                        Thread.sleep(16);
                    }
                    catch(final InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
