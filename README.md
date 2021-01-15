extract sql insert into for all version <5.4.13 , R3 , >R5.4.13
tables transceiver, linkdepartment, subcell, subcelltrxmap, trxmap .




example :

INSERT INTO `basestation`(`BSNAME`,`BSADDRESS`) VALUES ( 'A','pricerbs01',)
INSERT INTO `basestation`(`BSNAME`,`BSADDRESS`) VALUES ( 'B','pricerbs02',)
INSERT INTO `basestation`(`BSNAME`,`BSADDRESS`) VALUES ( 'C','pricerbs03',)
INSERT INTO `basestation`(`BSNAME`,`BSADDRESS`) VALUES ( 'D','pricerbs04',)




INSERT INTO `link_department`(`ID`,`ALIAS`,`TRX_GROUP`,`IS_BACKOFFICE`) VALUES ( 'A01','','A|110:1,A|139:1,A|140:1,A|142:1,A|143:1,A|144:1,A|145:1,A|146:1,A|169:1','0',)
INSERT INTO `link_department`(`ID`,`ALIAS`,`TRX_GROUP`,`IS_BACKOFFICE`) VALUES ( 'A02','Eaux-Grignotage','A|146:1,A|148:1,A|149:1,A|150:1,A|151:1,A|152:1,A|153:1,A|154:1,A|155:1,A|170:1','1',)
INSERT INTO `link_department`(`ID`,`ALIAS`,`TRX_GROUP`,`IS_BACKOFFICE`) VALUES ( 'B01','Cave','B|2:1,B|37:1,B|68:1,B|80:1,B|84:1,B|88:1,B|90:1,B|94:1,B|98:1','0',)
INSERT INTO `link_department`(`ID`,`ALIAS`,`TRX_GROUP`,`IS_BACKOFFICE`) VALUES ( 'B02','Surgelés-UF','B|2:1,B|5:1,B|12:1,B|16:1,B|22:1,B|26:1,B|33:1','0',)
INSERT INTO `link_department`(`ID`,`ALIAS`,`TRX_GROUP`,`IS_BACKOFFICE`) VALUES ( 'C02','Fruits - Poissonnerie','C|3:1,C|6:1,C|9:1,C|13:1,C|17:1,C|23:1,C|59:1,C|62:1,C|102:1','0',)
INSERT INTO `link_department`(`ID`,`ALIAS`,`TRX_GROUP`,`IS_BACKOFFICE`) VALUES ( 'D01','Bazar-EPCS','D|7:1,D|10:1,D|35:1,D|39:1,D|42:1,D|70:1,D|73:1,D|77:1,D|86:1,D|96:1,D|100:1,D|103:1','0',)



TRUNCATE table transceiver
INSERT INTO `transceiver`(`ID`,`TRXBSNAMEREF`,`TRXPORTNUM`,`TRXCABLEINDEX`,`TRXHWID`)VALUES ( '2','B','10','1','null')
INSERT INTO `transceiver`(`ID`,`TRXBSNAMEREF`,`TRXPORTNUM`,`TRXCABLEINDEX`,`TRXHWID`)VALUES ( '3','C','10','1','null')
INSERT INTO `transceiver`(`ID`,`TRXBSNAMEREF`,`TRXPORTNUM`,`TRXCABLEINDEX`,`TRXHWID`)VALUES ( '5','B','11','1','null')
INSERT INTO `transceiver`(`ID`,`TRXBSNAMEREF`,`TRXPORTNUM`,`TRXCABLEINDEX`,`TRXHWID`)VALUES ( '6','C','11','1','null')
INSERT INTO `transceiver`(`ID`,`TRXBSNAMEREF`,`TRXPORTNUM`,`TRXCABLEINDEX`,`TRXHWID`)VALUES ( '7','D','11','1','null')
INSERT INTO `transceiver`(`ID`,`TRXBSNAMEREF`,`TRXPORTNUM`,`TRXCABLEINDEX`,`TRXHWID`)VALUES ( '9','C','12','1','null')
INSERT INTO `transceiver`(`ID`,`TRXBSNAMEREF`,`TRXPORTNUM`,`TRXCABLEINDEX`,`TRXHWID`)VALUES ( '10','D','12','1','null')
INSERT INTO `transceiver`(`ID`,`TRXBSNAMEREF`,`TRXPORTNUM`,`TRXCABLEINDEX`,`TRXHWID`)VALUES ( '12','B','13','1','null')
INSERT INTO `transceiver`(`ID`,`TRXBSNAMEREF`,`TRXPORTNUM`,`TRXCABLEINDEX`,`TRXHWID`)VALUES ( '13','C','13','1','null')