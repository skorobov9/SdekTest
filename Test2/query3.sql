select stack.Customers.name from stack.Customers
join stack.Orders on Customers.row_id = customer_id
join stack.OrderItems on Orders.row_id = OrderItems.order_id
where YEAR(Orders.registered_at)=2020
group by stack.Customers.name
having ( COUNT(distinct order_id) = 
( select COUNT(distinct case when OrderItems.name='Кассовый аппарат' then order_id end)))
