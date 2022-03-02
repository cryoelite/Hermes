-- ************************************** `messageData`

CREATE TABLE `messageData`
(
 `messageContentID` varchar(128) NOT NULL ,
 `messageContent`   nvarchar(2000) NULL ,
 `media`            mediumblob NULL ,

PRIMARY KEY (`messageContentID`)
);