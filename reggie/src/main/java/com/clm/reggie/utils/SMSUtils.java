package com.clm.reggie.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import com.aliyuncs.dysmsapi.model.v20170525.*;


public class SMSUtils {

		public static void sendMessage(String phoneNumbers,String param){
		DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI5tDH6D5PTDbvp65uu4wk", "RYXN3BIX7dm3AUp75LduIHpFk27Ytz");
		/** use STS Token
		 DefaultProfile profile = DefaultProfile.getProfile(
		 "<your-region-id>",           // The region ID
		 "<your-access-key-id>",       // The AccessKey ID of the RAM account
		 "<your-access-key-secret>",   // The AccessKey Secret of the RAM account
		 "<your-sts-token>");          // STS Token
		 **/

		IAcsClient client = new DefaultAcsClient(profile);


		SendSmsRequest request = new SendSmsRequest();
		request.setSignName("阿里云短信测试");
		request.setTemplateCode("SMS_154950909");
		request.setPhoneNumbers(phoneNumbers);
		request.setTemplateParam("{\"code\":\""+param+"\"}");

		try {
			SendSmsResponse response = client.getAcsResponse(request);
			System.out.println(new Gson().toJson(response));
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (ClientException e) {
			System.out.println("ErrCode:" + e.getErrCode());
			System.out.println("ErrMsg:" + e.getErrMsg());
			System.out.println("RequestId:" + e.getRequestId());
		}
		}

}