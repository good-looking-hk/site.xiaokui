listMenuByRoleId
===
* 留作提醒
* select * from sys_menu inner join sys_role_menu on sys_menu.id = 
* sys_role_menu.menu_id where sys_role_menu.role_id = #roleId#
select m.* from sys_menu m inner join sys_role_menu s on m.id = s.menu_id where s.role_id = #roleId#

findPermissionsByRoleId
===
select url from sys_menu m inner join sys_role_menu s
on m.id = s.menu_id 
where s.role_id = #roleId#
