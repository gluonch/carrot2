
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package carrot2.demo.settings;

import java.awt.Frame;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import carrot2.demo.ProcessSettings;
import carrot2.demo.ProcessSettingsBase;

import com.dawidweiss.carrot.filter.stc.StcConstants;
import com.dawidweiss.carrot.filter.stc.StcParameters;

/**
 * Settings class for HAOG-STC with a generic input.
 * 
 * HAOG uses settings almoust identical to STC.
 */
public class HaogStcSettings extends ProcessSettingsBase implements ProcessSettings  {

    public HaogStcSettings() {
        this.params = new StcParameters().toMap();
        //default value for HAOG-STC
        this.params.put(StcConstants.MERGE_THRESHOLD, String.valueOf(0.75));
    }

    private HaogStcSettings(Map params) {
        this.params = new HashMap(params);
    }

    public ProcessSettings createClone() {
        synchronized (this) {
            return new HaogStcSettings(params);
        }
    }

    public JComponent getSettingsComponent() {
        return new HaogStcSettingsDialog(this);
    }

	public JComponent getSettingsComponent(Frame owner) {
        return new HaogStcSettingsDialog(this);
	}

	public boolean isConfigured() {
		return true;
	}
	
	public boolean hasSettings() {
        return true;
    }

}