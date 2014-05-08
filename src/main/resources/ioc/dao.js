var ioc = {
	dataSource : {
		type : "com.alibaba.druid.pool.DruidDataSource",
		events : {
			depose : 'close'
		},
		fields : {
			// 请修改下面的数据库连接信息
			driverClassName : "org.postgresql.Driver",
			url : 'jdbc:postgresql://localhost:5432/postgres?charSet=UNICODE',
			username : 'postgres',
			password : 'postgres',
			maxActive : 20,
			validationQuery : "SELECT 'x'",
			testWhileIdle : true,
			testOnBorrow : false,
			testOnReturn : false
		}
	},

	dao : {
		type : 'org.nutz.dao.impl.NutDao',
		args : [ {
			refer : 'dataSource'
		} ]
	}
};