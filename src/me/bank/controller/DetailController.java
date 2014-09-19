package me.bank.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.bank.config.Constants;
import me.bank.kit.ParaKit;
import me.bank.model.Detail;
import me.bank.model.Teller;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

/**
 * CardController
 * 
 * 柜员管理
 * 
 */
public class DetailController extends Controller {

	public void index() {
		
		// 判断当前是否是搜索的数据进行的分页
		// 如果是搜索的数据，则跳转至search方法处理
		if (!ParaKit.isEmpty(getPara("s"))) {

			search();

			return;
		}

		int page = ParaKit.paramToInt(getPara("p"), 1);

		if (page < 1) {
			page = 1;
		}
		// 读取所有的柜员
		Page<Detail> detailList = Detail.dao.paginate(page, Constants.PAGE_SIZE);
		setAttr("detailList", detailList);
		setAttr("searchUuid", "");
		setAttr("searchIdentity", "");
		setAttr("searchPage", Constants.NOT_SEARCH_PAGE);
		render("index.html");
	}
	/**
	 * 检索
	 */
	
	public void search(){
		if(getPara("uuid")!=null && getPara("identity")!=null){
			setAttr("errorMsg", "只能按一类检索");
			render("index.html");
		}
		if (ParaKit.isEmpty(getPara("s"))) {

			// 说明当前请求是搜索数据的post请求，并非搜索的分页请求
			// 在这里执行搜索操作，并将结果保存到缓存中

			Map<String, String> queryParams = new HashMap<String, String>();
			queryParams.put("uuid", getPara("uuid"));
			queryParams.put("identity", getPara("identity"));
			setSessionAttr(Constants.SEARCH_SESSION_KEY, queryParams);

		}

		int page = ParaKit.paramToInt(getPara("p"), 1);

		if (page < 1) {
			page = 1;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("from detail where id > 0");

		HashMap<String, String> queryParams = getSessionAttr(Constants.SEARCH_SESSION_KEY);
		List<Object> params = new ArrayList<Object>();

		if (queryParams != null) {

			String uuid = queryParams.get("uuid");

			if (!ParaKit.isEmpty(uuid)) {
				sb.append(" and uuid = ?");
				params.add(uuid);
			}

			String identity = queryParams.get("identity");

			if (!ParaKit.isEmpty(identity)) {
				sb.append(" and identity = ?");
				params.add(identity);
			}


			setAttr("searchUuid", uuid);
			setAttr("searchIdentity", identity);
			setAttr("searchPage", Constants.SEARCH_PAGE);
			
		}
		
		// 柜员列表
		Page<Detail> detailList = Detail.dao.paginate(page, Constants.PAGE_SIZE, "select *",
				sb.toString(), params.toArray());

		setAttr("detailList", detailList);

		render("index.html");
	}

}
