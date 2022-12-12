package com.ruoyi;


import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.List;

/**
 * @说明 SNMP4J测试
 * @author cuisuqiang
 * @version 1.0
 * @since
 */
public class SnmpUtil {
	private Snmp snmp = null;
	private Address targetAddress = null;
	String ip;
	String value;
	String oid;
	String community;
	protected Logger logger = LoggerFactory.getLogger(getClass());

	public void initComm(String ip,String community) throws IOException {

		this.community=community;
		this.oid=oid;
		this.value=value;

		try {
//			this.ip= DES.decryptMode(ip);
			this.ip=ip;
		} catch (Exception e) {
			e.printStackTrace();
			this.ip=ip;
		}
		// 设置Agent方的IP和端口
		targetAddress = GenericAddress.parse("udp:"+this.ip+"/161");
		TransportMapping transport = new DefaultUdpTransportMapping();


		snmp = new Snmp(transport);
		transport.listen();
	}

	public ResponseEvent sendPDU(PDU pdu) throws IOException {
		// 设置 目标
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString(community));
		target.setAddress(targetAddress);
		// 通信不成功时的重试次数 N+1次
		target.setRetries(2);
		// 超时时间
		target.setTimeout(2 * 1000);
		// SNMP 版本
		target.setVersion(SnmpConstants.version2c);
		// 向Agent发送PDU，并返回Response
		return snmp.send(pdu, target);
	}

	public void getPDU(String oid) throws IOException {
		// PDU 对象
		PDU pdu = new PDU();
		pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 2, 1, 1, 5, 0 })));
		pdu.setType(PDU.GET);
		readResponse(sendPDU(pdu));
	}

	@SuppressWarnings("unchecked")
	public void readResponse(ResponseEvent respEvnt) {
		// 解析Response
		logger.error("------------>Response<-------------");

		if (respEvnt != null && respEvnt.getResponse() != null) {
		 List<? extends VariableBinding> recVBs = respEvnt.getResponse().getVariableBindings();
			System.out.println(JSONUtil.toJsonStr(recVBs));
			for (int i = 0; i < recVBs.size(); i++) {
				VariableBinding recVB = recVBs.get(i);
			}
		}

		if(snmp!=null) {
			try {
				snmp.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
//		SnmpUtil util = new SnmpUtil();
//		try {
//			util.initComm("192.168.10.108", "public");
//			util.getPDU(".1.3.6.1.2.1.1.5.0");
//
//		} catch (IOException e) {
//		}

//		List nameList = SnmpWalkTest.snmpAsynWalk("172.21.32.30", "public",
//				".1.3.6.1.4.1.2011.6.128.1.1.2.43.1.3");
//		List nameList = SnmpWalk.snmpAsynWalk("192.168.10.108", "public", "1.3.6.1.2.1.1.5.0");
//		System.out.println(JSONUtil.toJsonStr(nameList));

	}
}
