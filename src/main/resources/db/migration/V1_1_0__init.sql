CREATE TABLE OWNER (
      ID BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT
    , CREATED_AT DATETIME NOT NULL
    , MODIFIED_AT DATETIME NOT NULL
    , USERNAME VARCHAR (60) NOT NULL
    , PWD VARCHAR (120) NOT NULL
    , UNIQUE (USERNAME)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8MB4;

CREATE TABLE INVOICE (
      ID BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT
    , CREATED_AT DATETIME NOT NULL
    , MODIFIED_AT DATETIME NOT NULL
    , USER_ID BIGINT NOT NULL
    , INV_FROM DATE NOT NULL
    , INV_TO DATE NOT NULL
    , INV_EXPIRE DATE NOT NULL
    , INV_CREATED_DATE DATE NOT NULL
    , AMOUNT DECIMAL(10, 2) NOT NULL
    , PERCENT DECIMAL(3, 2) NOT NULL
    , FINAL_AMOUNT DECIMAL(10, 2) NOT NULL
    , COMPANY VARCHAR (60) NOT NULL
    , INV_NR VARCHAR (10) NOT NULL
    , INDEX (CREATED_AT)
    , INDEX (USER_ID)
    , INDEX (INV_FROM)
    , INDEX (INV_TO)
    , INDEX (INV_EXPIRE)
    , INDEX (INV_CREATED_DATE)
    , INDEX (AMOUNT)
    , INDEX (PERCENT)
    , INDEX (FINAL_AMOUNT)
    , INDEX (COMPANY)
    , INDEX (INV_NR)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8MB4;
