package com.joe.main.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joe.http.HttpClientUtils;
import com.joe.main.exception.InitError;
import com.joe.main.exception.LoginTimeOutError;
import com.joe.main.provider.DefaultMsgProccesser;
import com.joe.main.provider.TextMsgProccesser;
import com.joe.main.session.WechatSession;
import com.joe.main.task.InitTask;
import com.joe.main.task.ListenerTask;
import com.joe.main.task.LoginInitTask;
import com.joe.main.task.LoginTask;
import com.joe.main.task.QRIdTask;
import com.joe.main.task.QRTask;
import com.joe.main.task.RetryTask;
import com.joe.main.task.StatusNotify;
import com.joe.tools.JsonParser;
import com.joe.tools.XmlParser;

@Service
public class CoreService {
	@Autowired
	private HttpClientUtils httpClientUtils;
	@Autowired
	private BaseService baseService;
	@Autowired
	private JsonParser jsonParser;
	@Autowired
	private XmlParser xmlParser;
	@Autowired
	private MsgService msgService;
	@Autowired
	private TextMsgProccesser textMsgProccesser;

	/**
	 * 
	 * @throws LoginTimeOutError
	 * @throws UnsupportedEncodingException
	 */
	public void run() throws LoginTimeOutError {
		// 获取二维码
		String path = "D://";
		String name = "qrcode.jpg";
		System.out.println("\n\n---------------------------------------");
		System.out.println("您的机器人正在启动，请打开" + path + "准备扫描二维码登录;\n具体操作：打开上边的路径，然后打开手机微信扫一扫，准备扫描二维码，二维码将在5S后生成");
		System.out.println("---------------------------------------\n\n");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		WechatSession wechatSession = new WechatSession(path, name, String.valueOf(System.currentTimeMillis()));
		LoggerService.info("二维码保存路径：{}；二维码名称：{}", path, name);
		System.out.println(String.format("二维码保存路径：%s；二维码名称：%s", path, name));
		// 获取QRid
		QRIdTask qRIdTask = new QRIdTask();
		// 获取QR
		QRTask qRTask = new QRTask();
		// 登陆
		LoginTask loginTask = new LoginTask();
		// 登陆初始化
		LoginInitTask loginInitTask = new LoginInitTask();
		// 初始化
		InitTask initTask = new InitTask();
		// 通知
		StatusNotify statusNotify = new StatusNotify();
		// 开始监听
		ListenerTask listenerTask = new ListenerTask();
		// 注册
		msgService.register(textMsgProccesser);
		msgService.register(DefaultMsgProccesser.class);

		List<RetryTask> tasks = new ArrayList<RetryTask>(7);
		tasks.add(qRIdTask);
		tasks.add(qRTask);
		tasks.add(loginTask);
		tasks.add(loginInitTask);
		tasks.add(initTask);
		tasks.add(statusNotify);
		tasks.add(listenerTask);

		// 初始化
		init(tasks);
		run(tasks, wechatSession);
	}

	/**
	 * 
	 * 运行tasks
	 * 
	 * @param tasks
	 *            要运行的tasks
	 * @param wechatSession
	 *            微信session
	 * @throws LoginTimeOutError
	 *             登录超时（二维码过期）会抛出该error
	 * @throws InitError
	 *             读取消息失败时会抛出该error
	 */
	private void run(List<RetryTask> tasks, WechatSession wechatSession) throws LoginTimeOutError, InitError {
		// 监听前的初始化
		for (int i = 0; i < tasks.size() - 1; i++) {
			RetryTask task = tasks.get(i);
			RetryService.retryIgnoreException(10, task, wechatSession);
			if (!task.isComplete()) {
				throw new InitError(task.getTaskName());
			}
			LoggerService.info("{} 成功", task.getTaskName());
			if(task instanceof QRTask){
				System.out.println("\n\n--------------------------------------------------------");
				System.out.println("请打开上边指定名称的二维码，使用手机微信扫一扫登录您的微信机器人");
				System.out.println("--------------------------------------------------------\n\n");
			}
		}
		// 开始监听
		RetryTask listen = tasks.get(tasks.size() - 1);
		LoggerService.info("开始监听消息................");
		System.out.println("\n\n--------------------------------------------------------");
		System.out.println("oh 您的机器人上线了，赶紧召集好友来玩耍吧~~");
		System.out.println("--------------------------------------------------------\n\n");
		while (!listen.isComplete()) {
			RetryService.retryIgnoreException(10, listen, wechatSession);
		}
		LoggerService.info("微信机器人关闭");
	}

	/**
	 * 初始化task
	 * 
	 * @param tasks
	 */
	private void init(List<RetryTask> tasks) {
		for (RetryTask task : tasks) {
			task.init(httpClientUtils, baseService, jsonParser, xmlParser, msgService);
		}
	}
}
