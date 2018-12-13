DELIMITER $$
CREATE PROCEDURE `seckill`.`execute_seckill`
	(in v_id VARCHAR(36),in v_kill_product_id VARCHAR(36),in v_mobile BIGINT,in v_kill_time TIMESTAMP,out r_result int)
	BEGIN
		DECLARE insert_count int DEFAULT 0;
    START TRANSACTION;
    INSERT IGNORE INTO kill_item(id,kill_product_id,mobile) values(v_id,v_kill_product_id,v_mobile);
    SELECT ROW_COUNT() INTO insert_count;
		IF(insert_count = 0) THEN
			ROLLBACK;
			SET r_result = -1;
		ELSEIF(insert_count < 0) THEN
			ROLLBACK;
			SET r_result = -2;
		ELSE
			UPDATE kill_product SET number = number - 1
			WHERE id = v_kill_product_id AND number >= 1 AND end_time > v_kill_time AND start_time < v_kill_time;
			SELECT ROW_COUNT() INTO insert_count;
			IF(insert_count = 0) THEN
				ROLLBACK;
				SET r_result = 0;
			ELSEIF(insert_count < 0) THEN
				ROLLBACK;
				SET r_result = -2;
			ELSE
				COMMIT;
				SET r_result = 1;
			END IF;
		END IF;
	END;
$$
DELIMITER ;

delimiter $$
create procedure 'newxiaokui'.'execute_seckil'
  (in i_seckill_product_id int, in i_phone varchar(32), in i_seckill_time datetime, out o_result int)
  begin
    declare insert_count int default 0;
    start transaction ;
    insert ignore into success_seckilled(seckill_product_id, phone, seckill_time) values(i_seckill_product_id, i_phone, i_seckill_time);
    select row_count() into insert_count;
    if (insert_count = 0) then
      rollback ;
      set o_result = -1;
    elseif (insert_count < 0) then
      rollback;
      set o_result = -2;
    else
      update seckill_product set number = number - 1
      where id = i_seckill_product_id and number >= 1 and end_time > i_seckill_time and start_time < i_seckill_time;
      select row_count() into insert_count;
      if (insert_count = 0) then
        rollback ;
        set o_result = 0;
      elseif (insert_count < 0) then
        rollback ;
        set o_result = -2;
      else
        commit ;
        set o_result = 1;
      end if;
    end if;
  end;
$$
delimiter ;

