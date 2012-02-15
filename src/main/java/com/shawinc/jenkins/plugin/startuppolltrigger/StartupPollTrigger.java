package com.shawinc.jenkins.plugin.startuppolltrigger;

import antlr.ANTLRException;
import hudson.Extension;
import hudson.model.BuildableItem;
import hudson.model.Hudson;
import hudson.model.Item;
import hudson.model.Project;
import hudson.model.listeners.ItemListener;
import hudson.scm.NullSCM;
import hudson.triggers.SCMTrigger;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 *
 * @author gcampb2
 */
public class StartupPollTrigger extends Trigger<BuildableItem>
{
	@DataBoundConstructor
	public StartupPollTrigger()
	{
		super();
	}
	@Extension
	public static class DescriptorImpl extends TriggerDescriptor
	{
		@Override
		public boolean isApplicable(Item item) {
			return true;
		}

		@Override
		public String getDisplayName() {
			return "Poll CVS when Jenkins starts up";
		}
	}

	@Extension
	public static final class ListenerImpl extends ItemListener
	{
	    private static final Logger LOGGER = Logger.getLogger(ListenerImpl.class.getName());

		@Override
		public void onLoaded()
		{
			LOGGER.info("Scanning for StartupPollTriggers");
			Iterator<Project> projects = Hudson.getInstance().getProjects().iterator();
			while (projects.hasNext())
			{
				Project project = projects.next();
				if (project.getTrigger(StartupPollTrigger.class) != null)
				{
					LOGGER.info("Found StartupPollTrigger on " + project.getName());
					if (!project.isInQueue() && !project.isBuilding())
					{
						// not already running, so get polling trigger & fire
						SCMTrigger t = (SCMTrigger)project.getTrigger(SCMTrigger.class);
						if (t != null)
						{
							// poll
							LOGGER.info("StartupPollTrigger Polling " + project.getName());
							t.run();
						}
						else if (project.getScm() != null && !(project.getScm() instanceof NullSCM) )
						{
							try {
								t = new SCMTrigger(""); // don't give it a schedule
								t.start(project, true); // makes trigger->project tie, but not vice versa!
								t.run();
								t.stop(); // currently a noop, but just in case...
							} catch (ANTLRException ex) {
								Logger.getLogger(StartupPollTrigger.class.getName()).log(Level.SEVERE, "Failed to poll at startup - Antlr Exception", ex);
							}
						}
					}
				}
			}
		}
	}
}
