package com.founder.sso.validate;

import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.founder.sso.service.SensitiveWordService;

@Component
public class SentiveWordValidator  implements ConstraintValidator<Sentive, String>{

	@Autowired
	private SensitiveWordService sensitiveWordService;
	
	@Override
	public void initialize(Sentive arg0) {
	}

	@Override
	public boolean isValid(String arg0, ConstraintValidatorContext arg1) {
		if(arg0==null||!StringUtils.hasText(arg0.trim())){
			return true;
		}
		arg0 = arg0.trim();
		SensitivewordUtil swUtil = new SensitivewordUtil();
		Set<String> keyWordSet = sensitiveWordService.readSensitiveWordTable();
		swUtil.addSensitiveWordToHashMap(keyWordSet);
		if(swUtil.isContaintSensitiveWord(arg0,2)){
			return false;
		}
		return true;
	}

}
