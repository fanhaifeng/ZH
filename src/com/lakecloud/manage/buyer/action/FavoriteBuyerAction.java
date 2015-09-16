package com.lakecloud.manage.buyer.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lakecloud.core.annotation.SecurityMapping;
import com.lakecloud.core.domain.virtual.SysMap;
import com.lakecloud.core.mv.JModelAndView;
import com.lakecloud.core.query.support.IPageList;
import com.lakecloud.core.security.support.SecurityUserHolder;
import com.lakecloud.core.tools.CommUtil;
import com.lakecloud.foundation.domain.Favorite;
import com.lakecloud.foundation.domain.Goods;
import com.lakecloud.foundation.domain.Store;
import com.lakecloud.foundation.domain.query.FavoriteQueryObject;
import com.lakecloud.foundation.service.IFavoriteService;
import com.lakecloud.foundation.service.IGoodsService;
import com.lakecloud.foundation.service.IStoreService;
import com.lakecloud.foundation.service.ISysConfigService;
import com.lakecloud.foundation.service.IUserConfigService;

@Controller
public class FavoriteBuyerAction {
	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IFavoriteService favoriteService;
	@Autowired
	private IStoreService storeService;
	@Autowired
	private IGoodsService goodsService;

	/**
	 * Favorite列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "用户商品收藏", value = "/buyer/favorite_goods.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/favorite_goods.htm")
	public ModelAndView favorite_goods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/favorite_goods.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		FavoriteQueryObject qo = new FavoriteQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.addQuery("obj.type", new SysMap("type", 0), "=");
		qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder
				.getCurrentUser().getId()), "=");
		IPageList pList = this.favoriteService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/buyer/favorite_goods.htm",
				"", params, pList, mv);
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "用户店铺收藏", value = "/buyer/favorite_store.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/favorite_store.htm")
	public ModelAndView favorite_store(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/favorite_store.html", configService
						.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if (url == null || url.equals("")) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		FavoriteQueryObject qo = new FavoriteQueryObject(currentPage, mv,
				orderBy, orderType);
		qo.addQuery("obj.type", new SysMap("type", 1), "=");
		qo.addQuery("obj.user.id", new SysMap("user_id", SecurityUserHolder
				.getCurrentUser().getId()), "=");
		IPageList pList = this.favoriteService.list(qo);
		CommUtil.saveIPageList2ModelAndView(url + "/buyer/favorite_store.htm",
				"", params, pList, mv);
		return mv;
	}

	@SecurityMapping(title = "用户收藏删除", value = "/buyer/favorite_del.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/favorite_del.htm")
	public String favorite_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage,
			int type) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Favorite favorite = this.favoriteService.getObjById(Long
						.parseLong(id));
				this.favoriteService.delete(Long.parseLong(id));
				if(type == 0){
					Goods good = favorite.getGoods();
					good.setGoods_collect(good.getGoods_collect()-1);
					this.goodsService.update(good);
				}else{
					Store store = favorite.getStore();
					store.setFavorite_count(store.getFavorite_count() - 1);
					this.storeService.update(store);
				}
			}
		}
		if (type == 0)
			return "redirect:favorite_goods.htm?currentPage=" + currentPage;
		else
			return "redirect:favorite_store.htm?currentPage=" + currentPage;
	}

}