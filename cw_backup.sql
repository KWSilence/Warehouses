--------------------------------------------------------
--  File created - Friday-April-23-2021   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Type CONFRONTATION
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TYPE "CONFRONTATION" as object
("DATE" varchar2(12),
count1 number,
count2 number);

/
--------------------------------------------------------
--  DDL for Type DEMANDFORECAST
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TYPE "DEMANDFORECAST" as table of forecast;

/
--------------------------------------------------------
--  DDL for Type FORECAST
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TYPE "FORECAST" as object
("DAY" number,
request number);

/
--------------------------------------------------------
--  DDL for Type GOODSCONFRONTATION
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TYPE "GOODSCONFRONTATION" as table of confrontation;

/
--------------------------------------------------------
--  DDL for Type GOODSTRANSFER
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TYPE "GOODSTRANSFER" as table of transfer;

/
--------------------------------------------------------
--  DDL for Type TRANSFER
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TYPE "TRANSFER" AS object
(id number,
name varchar2(50),
count number);

/
--------------------------------------------------------
--  DDL for Sequence GOODS_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "GOODS_SEQ"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence SALES_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "SALES_SEQ"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence WAREHOUSE1_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "WAREHOUSE1_SEQ"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence WAREHOUSE2_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  "WAREHOUSE2_SEQ"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Table GOODS
--------------------------------------------------------

  CREATE TABLE "GOODS" 
   (	"ID" NUMBER(*,0), 
	"NAME" VARCHAR2(50 BYTE), 
	"PRIORITY" FLOAT(126)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table SALES
--------------------------------------------------------

  CREATE TABLE "SALES" 
   (	"ID" NUMBER(*,0), 
	"GOOD_ID" NUMBER(*,0), 
	"GOOD_COUNT" NUMBER(*,0), 
	"CREATE_DATE" TIMESTAMP (3)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table USERS
--------------------------------------------------------

  CREATE TABLE "USERS" 
   (	"LOGIN" VARCHAR2(100 BYTE), 
	"PASSWORD" VARCHAR2(200 BYTE)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table WAREHOUSE1
--------------------------------------------------------

  CREATE TABLE "WAREHOUSE1" 
   (	"ID" NUMBER(*,0), 
	"GOOD_ID" NUMBER(*,0), 
	"GOOD_COUNT" NUMBER(*,0)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table WAREHOUSE2
--------------------------------------------------------

  CREATE TABLE "WAREHOUSE2" 
   (	"ID" NUMBER(*,0), 
	"GOOD_ID" NUMBER(*,0), 
	"GOOD_COUNT" NUMBER(*,0)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for View VIEW1
--------------------------------------------------------

  CREATE OR REPLACE FORCE EDITIONABLE VIEW "VIEW1" ("ID", "NAME", "GOOD_COUNT") AS 
  select goods.id, goods.name, warehouse1.good_count from goods
join warehouse1 on goods.id = warehouse1.good_id
where good_count < 5
;
--------------------------------------------------------
--  DDL for View VIEW2
--------------------------------------------------------

  CREATE OR REPLACE FORCE EDITIONABLE VIEW "VIEW2" ("NAME", "count") AS 
  select goods.name, sum(good_count) as "count" from goods
join sales on goods.id = sales.good_id
where extract(month from create_date) = '02'
group by goods.id, goods.name
order by sum(good_count) desc
FETCH NEXT 5 ROWS ONLY
;
--------------------------------------------------------
--  DDL for Index GOODS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "GOODS_PK" ON "GOODS" ("ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index SALES_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "SALES_PK" ON "SALES" ("ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index USERS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "USERS_PK" ON "USERS" ("LOGIN") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index WAREHOUSE2_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "WAREHOUSE2_PK" ON "WAREHOUSE2" ("ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index WAREHOUSE_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "WAREHOUSE_PK" ON "WAREHOUSE1" ("ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Trigger CHECKCOUNTFORSALES
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TRIGGER "CHECKCOUNTFORSALES" before insert on sales
FOR EACH ROW
declare
    sum_count number;
begin
    select (coalesce(warehouse1.good_count,0)+coalesce(warehouse2.good_count,0)) into sum_count from goods
    left join warehouse1 on warehouse1.good_id=goods.id
    left join warehouse2 on warehouse2.good_id=goods.id
    where goods.id = :new.good_id;

    if :new.good_count > sum_count then
        raise_application_error(-20200, 'current good_count to sell more than in both warehouses');
    end if;
end;
/
ALTER TRIGGER "CHECKCOUNTFORSALES" ENABLE;
--------------------------------------------------------
--  DDL for Trigger CHECKCOUNTFORSALES1
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TRIGGER "CHECKCOUNTFORSALES1" before insert on sales
FOR EACH ROW
begin
    if :new.good_count < 1 then
        raise_application_error(-20200, 'good_count should not be less than 1');
    end if;
end;
/
ALTER TRIGGER "CHECKCOUNTFORSALES1" ENABLE;
--------------------------------------------------------
--  DDL for Trigger CHECKGOODONDELETE
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TRIGGER "CHECKGOODONDELETE" before delete on goods
FOR EACH ROW
declare
    w1_count number;
    w2_count number;
    s_count number;
begin
    select count(*) into w1_count from warehouse1 where good_id = :old.id;
    select count(*) into w2_count from warehouse2 where good_id = :old.id;
    select count(*) into s_count from sales where good_id = :old.id;

    if w1_count > 0 then
        raise_application_error(-20200, 'this good has record on warehouse1');
    end if;
    if w2_count > 0 then
        raise_application_error(-20200, 'this good has record on warehouse2');
    end if;
    if s_count > 0 then
        raise_application_error(-20200, 'this good has record on sales');
    end if;
end;
/
ALTER TRIGGER "CHECKGOODONDELETE" ENABLE;
--------------------------------------------------------
--  DDL for Trigger CHECKWAREHOUSE2
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TRIGGER "CHECKWAREHOUSE2" before update on warehouse2
FOR EACH ROW
declare
    w1_count number;
begin
    select coalesce(warehouse1.good_count,0) into w1_count from goods
    left join warehouse1 on warehouse1.good_id=goods.id
    where goods.id= :new.good_id;

    if (w1_count > 0 and :new.good_count < :old.good_count) then
        raise_application_error(-20200, 'this good stored in warehouse1');
    end if;
end;
/
ALTER TRIGGER "CHECKWAREHOUSE2" ENABLE;
--------------------------------------------------------
--  DDL for Trigger GOODS_TRIG
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TRIGGER "GOODS_TRIG" BEFORE INSERT ON GOODS
FOR EACH ROW
      WHEN (NEW.ID IS NULL) BEGIN
    SELECT GOODS_SEQ.nextval INTO :NEW.ID FROM dual;
END;

/
ALTER TRIGGER "GOODS_TRIG" ENABLE;
--------------------------------------------------------
--  DDL for Trigger SALES_TRIG
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TRIGGER "SALES_TRIG" BEFORE INSERT ON SALES
FOR EACH ROW
      WHEN (NEW.ID IS NULL) BEGIN
    SELECT SALES_SEQ.nextval INTO :NEW.ID FROM dual;
END;

/
ALTER TRIGGER "SALES_TRIG" ENABLE;
--------------------------------------------------------
--  DDL for Trigger WAREHOUSE1_TRIG
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TRIGGER "WAREHOUSE1_TRIG" BEFORE INSERT ON WAREHOUSE1
FOR EACH ROW
      WHEN (NEW.ID IS NULL) BEGIN
    SELECT WAREHOUSE1_SEQ.nextval INTO :NEW.ID FROM dual;
END;

/
ALTER TRIGGER "WAREHOUSE1_TRIG" ENABLE;
--------------------------------------------------------
--  DDL for Trigger WAREHOUSE2_TRIG
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TRIGGER "WAREHOUSE2_TRIG" BEFORE INSERT ON WAREHOUSE2
FOR EACH ROW
      WHEN (NEW.ID IS NULL) BEGIN
    SELECT WAREHOUSE2_SEQ.nextval INTO :NEW.ID FROM dual;
END;

/
ALTER TRIGGER "WAREHOUSE2_TRIG" ENABLE;
--------------------------------------------------------
--  DDL for Procedure TAKEFROMWAREHOUSES
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "TAKEFROMWAREHOUSES" (g_id in number, g_count number) is
    c1 number;
    c2 number;
    tmp number;
begin
    select coalesce(warehouse1.good_count,0), coalesce(warehouse2.good_count,0) into c1, c2 from goods
    left join warehouse1 on warehouse1.good_id=goods.id
    left join warehouse2 on warehouse2.good_id=goods.id
    where goods.id = g_id;

    if (c1+c2) >= g_count
    then
        if (c1 > g_count)
        then
            update warehouse1 set good_count = good_count - g_count where good_id = g_id;
        else
            tmp := g_count - c1;
            update warehouse1 set good_count = 0 where good_id = g_id;
            update warehouse2 set good_count = good_count - tmp where good_id = g_id;
        end if;
    end if;
end;

/
--------------------------------------------------------
--  DDL for Function GETDEMANDFORECAST
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE FUNCTION "GETDEMANDFORECAST" (time1 in timestamp, time2 in timestamp, g_id in number, p_days in number)
    return DemandForecast pipelined as
    v_row forecast;
    cur_day number := 1;
    days_count number := TO_DATE(to_char(time2, 'dd.MM.yyyy'), 'dd.MM.yyyy')-TO_DATE(to_char(time1, 'dd.MM.yyyy'), 'dd.MM.yyyy') + 1;
    type NumberArray is varray(365) of number;
    days_arr NumberArray := NumberArray();
    days_num NumberArray := NumberArray();
    tmp_arr NumberArray;
    tmp_arr1 NumberArray;
    ind number;
begin
    --INIT ARRAY OF DAYS
    --dbms_output.put_line('Days count: '||days_count);
    for r in (select TO_DATE(to_char(create_date, 'dd.MM.yyyy'),'dd.MM.yyyy') as "DATE", sum(good_count) as "COUNT" from sales
        where good_id = g_id and create_date between time1 and time2
        group by good_id, TO_DATE(to_char(create_date, 'dd.MM.yyyy'),'dd.MM.yyyy')
        order by TO_DATE(to_char(create_date, 'dd.MM.yyyy'),'dd.MM.yyyy')) loop
        while cur_day < (r.date - to_date(to_char(time1,'dd.MM.yyyy'),'dd.MM.yyyy')+1)
        loop
            days_arr.extend();
            days_arr(cur_day) := 0;
            days_num.extend();
            days_num(cur_day) := cur_day;
            cur_day:=cur_day+1;
        end loop;
        days_arr.extend();
        days_arr(cur_day) := r.count;
        days_num.extend();
        days_num(cur_day) := cur_day;
        cur_day:=cur_day+1;
        --dbms_output.put_line(r.date - to_date(to_char(time1,'dd.mm.yyyy'))+1||' '||r.count);
    end loop;
    while cur_day <= days_count
    loop
        days_arr.extend();
        days_arr(cur_day) := 0;
        days_num.extend();
        days_num(cur_day) := cur_day;
        cur_day:=cur_day+1;
    end loop;

    --dbms_output.put_line('ARRAY:');
    --for i in days_arr.first..days_arr.last loop
        --dbms_output.put_line(days_num(i)||' '||days_arr(i));
    --end loop;

    --dbms_output.put_line('TEST:');
    while (days_arr.last > 2)
    loop
        tmp_arr := NumberArray();
        tmp_arr1 := NumberArray();
        cur_day := 1;
        ind := 1;
        days_count := days_arr.last;
        while (cur_day <= days_count)
        loop
            if (cur_day = days_count) then
                tmp_arr.extend();
                tmp_arr(ind) := days_arr(days_count);
                tmp_arr1.extend();
                tmp_arr1(ind) := days_num(days_count);
                exit;
            end if;
            tmp_arr.extend();
            tmp_arr(ind) := (days_arr(cur_day) + days_arr(cur_day + 1))/2;
            tmp_arr1.extend();
            tmp_arr1(ind) := days_num(cur_day);
            cur_day := cur_day + 2;
            ind := ind + 1;
        end loop;
        days_arr := tmp_arr;
        days_num := tmp_arr1;

        --dbms_output.put_line('A');
        --for i in days_arr.first..days_arr.last loop
        --    dbms_output.put_line(days_num(i)||' '||days_arr(i));
        --end loop;
    end loop;

    --dbms_output.put_line('Line');
    for i in 1..p_days loop
        --dbms_output.put_line(i||' '||((days_arr(2)-days_arr(1))*(i-days_num(1))/(days_num(2)-days_num(1))+days_arr(1)));
        v_row := forecast(i, ((days_arr(2)-days_arr(1))*(i-days_num(1))/(days_num(2)-days_num(1))+days_arr(1)));
        pipe row (v_row);
    end loop;
    return;
end;

/
--------------------------------------------------------
--  DDL for Function GETGOODSCONFRONTATION
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE FUNCTION "GETGOODSCONFRONTATION" (g1 in number, g2 in number)
    return GoodsConfrontation pipelined as
    v_row confrontation;
begin
    for r in(select g1_t.g1_date as "DATE", g1_t.g1_count as "COUNT1", g2_t.g2_count as "COUNT2" from
        (select to_char(sales.create_date, 'dd.mm.yyyy') as g1_date, sum(good_count) as g1_count from sales
        where sales.good_id=g1 group by to_char(sales.create_date, 'dd.mm.yyyy')) g1_t
        join (select to_char(sales.create_date, 'dd.mm.yyyy') as g2_date, sum(good_count) as g2_count from sales
        where sales.good_id=g2 group by to_char(sales.create_date, 'dd.mm.yyyy')) g2_t on g1_t.g1_date = g2_t.g2_date
        where g1_t.g1_count > g2_t.g2_count) loop
        v_row := confrontation(r.date, r.count1, r.count2);
        pipe row (v_row);
    end loop;
    return;
end;

/
--------------------------------------------------------
--  DDL for Function GETGOODSFORTRANSFER
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE FUNCTION "GETGOODSFORTRANSFER" return GoodsTransfer pipelined as
    v_row transfer;
begin
    for r in (select goods.id, goods.name, sum(sales.good_count) as count from goods
        join sales on sales.good_id=goods.id
        where create_date between to_date(to_char(sysdate-1, 'dd.mm.yyyy')) and sysdate  --[now-1 00:00; now)
        group by goods.id, goods.name, goods.priority
        order by goods.priority desc) loop
            v_row := transfer (r.id, r.name, r.count);
            pipe row (v_row);
        end loop;
    return;
end;

/
--------------------------------------------------------
--  DDL for Function GETGOODSFORTRANSFER1
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE FUNCTION "GETGOODSFORTRANSFER1" (g_count in number) 
    return GoodsTransfer pipelined as
    cur_count number := 0;
    v_row transfer;
begin
    for rec in (select * from table ( getGoodsForTransfer )) loop
        if ((cur_count+ rec.count) >= g_count) then
            v_row := transfer(rec.id, rec.name, g_count-cur_count);
            pipe row (v_row);
            return;
        end if;
        v_row := transfer(rec.id, rec.name, rec.count);
        pipe row (v_row);
        cur_count := cur_count + rec.count;
    end loop;
    return;
end;

/
--------------------------------------------------------
--  DDL for Function GETGOODSMAXREQUEST
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE FUNCTION "GETGOODSMAXREQUEST" (time1 in timestamp, time2 in timestamp) return number as
    res number;
begin
    select good_id into res from sales
    where create_date between time1 and time2
    group by good_id
    order by sum(good_count) desc
    fetch next 1 rows only;
    return(res);
end;

/
--------------------------------------------------------
--  Constraints for Table USERS
--------------------------------------------------------

  ALTER TABLE "USERS" ADD CONSTRAINT "USERS_PK" PRIMARY KEY ("LOGIN")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table WAREHOUSE1
--------------------------------------------------------

  ALTER TABLE "WAREHOUSE1" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "WAREHOUSE1" ADD CONSTRAINT "WAREHOUSE_PK" PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table SALES
--------------------------------------------------------

  ALTER TABLE "SALES" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "SALES" ADD CONSTRAINT "SALES_PK" PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table GOODS
--------------------------------------------------------

  ALTER TABLE "GOODS" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "GOODS" ADD CONSTRAINT "GOODS_PK" PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table WAREHOUSE2
--------------------------------------------------------

  ALTER TABLE "WAREHOUSE2" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "WAREHOUSE2" ADD CONSTRAINT "WAREHOUSE2_PK" PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table SALES
--------------------------------------------------------

  ALTER TABLE "SALES" ADD CONSTRAINT "FK_SALES_GOODS" FOREIGN KEY ("GOOD_ID")
	  REFERENCES "GOODS" ("ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table WAREHOUSE1
--------------------------------------------------------

  ALTER TABLE "WAREHOUSE1" ADD CONSTRAINT "FK_WAREHOUSE1_GOODS" FOREIGN KEY ("GOOD_ID")
	  REFERENCES "GOODS" ("ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table WAREHOUSE2
--------------------------------------------------------

  ALTER TABLE "WAREHOUSE2" ADD CONSTRAINT "FK_WAREHOUSE2_GOODS" FOREIGN KEY ("GOOD_ID")
	  REFERENCES "GOODS" ("ID") ENABLE;
