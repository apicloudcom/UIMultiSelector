//
//UZModule
//
//Modified by magic 16/2/23.
//Copyright (c) 2016年 APICloud. All rights reserved.
//
package com.apicloud.devlop.uzBle;

import java.util.UUID;

import org.json.JSONArray;

import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

public class JsParamsUtil {
	private static JsParamsUtil instance;

	public static JsParamsUtil getInstance() {
		if (instance == null) {
			instance = new JsParamsUtil();
		}
		return instance;
	}

	public UUID[] getUUIDS(UZModuleContext moduleContext) {
		JSONArray serviceUUIDs = moduleContext.optJSONArray("serviceUUIDs");
		if (serviceUUIDs != null && serviceUUIDs.length() > 0) {
			UUID[] uuids = new UUID[serviceUUIDs.length()];
			for (int i = 0; i < serviceUUIDs.length(); i++) {
				uuids[i] = UUID.fromString(serviceUUIDs.optString(i));
			}
			return uuids;
		}
		return null;
	}
}
