package com.teman.plurkdog.strategy;

import java.util.List;

import com.teman.plurkdog.bean.PlurkMsg;
import com.teman.plurkdog.bean.RespMsg;
import com.teman.plurkdog.service.PlurkBotService;
import com.teman.plurkdog.service.UserService;

public interface Strategy {

	public void procPlurks(List<PlurkMsg> plurks, PlurkBotService botService);

	
	 
}
