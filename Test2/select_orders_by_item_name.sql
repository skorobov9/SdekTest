create function stack.select_orders_by_item_name(@name NVARCHAR(MAX))
returns table
as 
return
(
SELECT Orders.row_id as order_id, Customers.name as customer, COUNT(OrderItems.row_id) as items_count  from stack.Orders join stack.Customers on
Customers.row_id=Orders.customer_id
join stack.OrderItems on order_id=Orders.row_id and  OrderItems.name=@name
group by Orders.row_id, Customers.name
);
