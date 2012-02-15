package com.shawinc.jenkins.plugin.startuppolltrigger;

import hudson.model.Cause;

/**
 *
 * @author gcampb2
 */
public class StartupPollTriggerCause extends Cause
{
    @Override
    public String getShortDescription()
    {
        return "Started because SCM changes found at Jenkins startup.";
    }
}
