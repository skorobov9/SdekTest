create function stack.calculate_total_price_for_orders_group(@row_id int)
returns int
as
begin
   DECLARE @OrderSum INT;
   with results (row_id) as (select stack.Orders.row_id  from stack.Orders where parent_id=@row_id or row_id=@row_id union all  
   select Orders.row_id FROM Orders join results on Orders.parent_id = results.row_id)
   select @OrderSum=SUM(price) from stack.OrderItems   
   where order_id in (select results.row_id from results)
	RETURN @OrderSum;
end;
