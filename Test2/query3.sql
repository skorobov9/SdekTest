select stack.Customers.name from stack.Customers
join stack.Orders on Customers.row_id = customer_id
join stack.OrderItems on Orders.row_id = OrderItems.order_id
where YEAR(Orders.registered_at)=2020
group by stack.Customers.name
having ( COUNT(order_id) = 
( select COUNT(stack.OrderItems.name) from stack.OrderItems where  name='Кассовый аппарат'))