package com.founder.sso.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotNullBothValidator implements ConstraintValidator<NotNullBoth,Login>{
 
    public void initialize(NotNullBoth nnb) {  
    }  
  
	public boolean isValid(Login loginInfo, ConstraintValidatorContext context) {	   
		boolean valid = (loginInfo.getPhone() == null) && (loginInfo.getEmail() == null);  
        return !valid;
	}  
}
