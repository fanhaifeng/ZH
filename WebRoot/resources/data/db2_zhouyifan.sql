/*
Data Transfer
Author: zhouyifan
company:江苏太湖云计算信息技术股份有限公司
Date: 2015-08-04 13:19:46
*/

-- ----------------------------
-- Create Datas  
-- ----------------------------
INSERT INTO DB2INST1.LAKECLOUD_PAYMENT(ADDTIME,DELETESTATUS,INSTALL,INTERFACETYPE,MARK,TRADE_MODE,TYPE) VALUES(current timestamp,0,1,0,'charge',0,'user');

--2015-08-05
UPDATE LAKECLOUD_SYSCONFIG SET EMAILENABLE=0

--2015-08-18
update lakecloud_Navigation set DISPLAY=0 where id
in (select id from lakecloud_Navigation
where display=1 and type!='sparegoods' and location=0 and title!='首页')

--2015-08-25
insert into DB2INST1.LAKECLOUD_ARTICLE(ID,ADDTIME,DELETESTATUS,CONTENT,DISPLAY,MARK,SEQUENCE,TITLE,URL,ARTICLECLASS_ID) VALUES((select max(id)+1 from LAKECLOUD_ARTICLE),current timestamp,0,'直降促销',1,'',0,'直降促销','',(select id from DB2INST1.LAKECLOUD_ARTICLECLASS where CLASSNAME='特色服务'));
insert into DB2INST1.LAKECLOUD_ARTICLE(ID,ADDTIME,DELETESTATUS,CONTENT,DISPLAY,MARK,SEQUENCE,TITLE,URL,ARTICLECLASS_ID) VALUES((select max(id)+1 from LAKECLOUD_ARTICLE),current timestamp,0,'货到付款',1,'',0,'货到付款','',(select id from DB2INST1.LAKECLOUD_ARTICLECLASS where CLASSNAME='支付方式'));
insert into DB2INST1.LAKECLOUD_ARTICLE(ID,ADDTIME,DELETESTATUS,CONTENT,DISPLAY,MARK,SEQUENCE,TITLE,URL,ARTICLECLASS_ID) VALUES((select max(id)+1 from LAKECLOUD_ARTICLE),current timestamp,0,'中金支付',1,'',0,'中金支付','',(select id from DB2INST1.LAKECLOUD_ARTICLECLASS where CLASSNAME='支付方式'));

--2015-09-09
insert into DB2INST1.LAKECLOUD_XCONF(ADDTIME,DELETESTATUS,XCONFKEY,XCONFVALUE) VALUES(current timestamp,0,'integration_from_register','1000');
insert into DB2INST1.LAKECLOUD_XCONF(ADDTIME,DELETESTATUS,XCONFKEY,XCONFVALUE) VALUES(current timestamp,0,'integration_from_invite','500');
insert into DB2INST1.LAKECLOUD_XCONF(ADDTIME,DELETESTATUS,XCONFKEY,XCONFVALUE) VALUES(current timestamp,0,'integration_from_order','1');
insert into DB2INST1.LAKECLOUD_XCONF(ADDTIME,DELETESTATUS,XCONFKEY,XCONFVALUE) VALUES(current timestamp,0,'integration_rate_for_money','0.5');
insert into DB2INST1.LAKECLOUD_XCONF(ADDTIME,DELETESTATUS,XCONFKEY,XCONFVALUE) VALUES(current timestamp,0,'integration_rate_for_order','0.5');
