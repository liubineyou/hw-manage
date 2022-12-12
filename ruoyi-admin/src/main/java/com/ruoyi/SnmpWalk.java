package com.ruoyi;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.hutool.json.JSONUtil;
import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

public class SnmpWalk{

	/**
	 *
	 * <img src="http://192.168.10.108:9090/test/hw-olt-mib.png" ></img>
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String oid = ".1.3.6.1.2.1.4.22";
		String ip="192.168.10.108";
		int port=161;
		testWalk(ip,port,oid);
	}

	private static void testWalk(String ip,int port,String oid){

		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString("public"));
		target.setAddress(GenericAddress.parse("udp:"+ip+"/"+port+"")); // supply your own IP and port
		target.setRetries(2);
		target.setTimeout(1500);
		target.setVersion(SnmpConstants.version2c);

		// linux -test
		Map<String, String> result = null; // ifTable, mib-2 interfaces
		try {
			result = doWalk(oid, target);
			System.out.println(JSONUtil.toJsonStr(result));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}



		//华为olt- hwGponDeviceOntConfigInfoTable sn状态

		//Map<String, String> result = doWalk(".1.3.6.1.4.1.2011.6.128.1.1.2.43", target); // ifTable, mib-2 interfaces
//		System.out.println(JSONUtil.toJsonStr(result));


//		Map<String, String> result = doWalk(".1.3.6.1.2.1.2.2", target); // ifTable, mib-2 interfaces

//		for (Map.Entry<String, String> entry : result.entrySet()) {
//			if (entry.getKey().startsWith(".1.3.6.1.2.1.2.2.1.2.")) {
//				System.out.println(
//						"ifDescr" + entry.getKey().replace(".1.3.6.1.2.1.2.2.1.2", "") + ": " + entry.getValue());
//			}
//			if (entry.getKey().startsWith(".1.3.6.1.2.1.2.2.1.3.")) {
//				System.out.println(
//						"ifType" + entry.getKey().replace(".1.3.6.1.2.1.2.2.1.3", "") + ": " + entry.getValue());
//			}
//		}
	}

	public static Map<String, String> doWalk(String tableOid, Target target) throws IOException {
		Map<String, String> result = new TreeMap<>();
		TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
		Snmp snmp = new Snmp(transport);
		transport.listen();

		TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
		List<TreeEvent> events = treeUtils.getSubtree(target, new OID(tableOid));
		if (events == null || events.size() == 0) {
			System.out.println("Error: Unable to read table...");
			return result;
		}

		for (TreeEvent event : events) {
			if (event == null) {
				continue;
			}
			if (event.isError()) {
				System.out.println("Error: table OID [" + tableOid + "] " + event.getErrorMessage());
				continue;
			}

			VariableBinding[] varBindings = event.getVariableBindings();
			if (varBindings == null || varBindings.length == 0) {
				continue;
			}
			for (VariableBinding varBinding : varBindings) {
				if (varBinding == null) {
					continue;
				}

				result.put("." + varBinding.getOid().toString(), varBinding.getVariable().toString());
			}

		}
		snmp.close();

		return result;
	}

}