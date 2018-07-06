# jmeter_report

1、录制好api接口脚本
2、jmeter -n -t name.jmx -l name.jtl
执行脚本，获得日志文件
3、输入日志文件，解析内容，若发现接口存在异常，发送邮件通知负责人
