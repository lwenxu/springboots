package com.ibeetl.admin.console.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.beetl.sql.core.engine.PageQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ibeetl.admin.console.service.OrgConsoleService;
import com.ibeetl.admin.console.service.RoleConsoleService;
import com.ibeetl.admin.console.service.UserConsoleService;
import com.ibeetl.admin.console.web.query.UserQuery;
import com.ibeetl.admin.console.web.query.UserRoleQuery;
import com.ibeetl.admin.core.annotation.Function;
import com.ibeetl.admin.core.annotation.Query;
import com.ibeetl.admin.core.entity.CoreUser;
import com.ibeetl.admin.core.entity.CoreUserRole;
import com.ibeetl.admin.core.service.CorePlatformService;
import com.ibeetl.admin.core.util.AnnotationUtil;
import com.ibeetl.admin.core.util.ConvertUtil;
import com.ibeetl.admin.core.util.DictUtil;
import com.ibeetl.admin.core.util.ValidateConfig;
import com.ibeetl.admin.core.util.enums.GeneralStateEnum;
import com.ibeetl.admin.core.web.JsonResult;

/**
 * 用户管理接口
 * 
 * @author xiandafu
 */
@Controller
public class UserConsoleController {
	private final Log log = LogFactory.getLog(this.getClass());
	private static final String MODEL = "/admin/user";

	@Autowired
	UserConsoleService userConsoleService;

	@Autowired
	CorePlatformService platformService;

	@Autowired
	RoleConsoleService roleConsoleService;
	@Autowired
	OrgConsoleService orgConsoleService;
	@Autowired
	DictUtil dictUtil;

	/* 页面 */

	@GetMapping(MODEL + "/index.do")
	@Function("user")
	public ModelAndView index() {
		ModelAndView view = new ModelAndView("/admin/user/index.html");
		view.addObject("search", UserQuery.class.getName());
		return view;
	}

	@GetMapping(MODEL + "/edit.do")
	@Function("user.edit")
	public ModelAndView edit(String id) {
		ModelAndView view = new ModelAndView("/admin/user/edit.html");
		CoreUser user = userConsoleService.queryById(id);
		view.addObject("user", user);
		return view;
	}

	@GetMapping(MODEL + "/add.do")
	@Function("user.add")
	public ModelAndView add() {
		ModelAndView view = new ModelAndView("/admin/user/add.html");
		return view;
	}

	@GetMapping(MODEL + "/changePassword.do")
	@Function("user.add")
	public ModelAndView changePassword(Long id) {
		CoreUser user = userConsoleService.queryById(id);
		ModelAndView view = new ModelAndView("/admin/user/changePassword.html");
		view.addObject("user", user);
		return view;
	}

	@GetMapping(MODEL + "/role/list.do")
	@Function("user.role")
	public ModelAndView userRoleIndex(Long id) {
		CoreUser user = userConsoleService.queryById(id);
		ModelAndView view = new ModelAndView("/admin/user/userRole.html");
		view.addObject("search", UserRoleQuery.class.getName());
		view.addObject("user", user);
		return view;
	}

	@GetMapping(MODEL + "/role/add.do")
	@Function("user.role")
	public ModelAndView userRoleAdd(Long id) {
		CoreUser user = userConsoleService.queryById(id);
		ModelAndView view = new ModelAndView("/admin/user/userRoleAdd.html");
		view.addObject("user", user);
		return view;
	}

	/* Json */

	@PostMapping(MODEL + "/delete.json")
	@Function("user.delete")
	@ResponseBody
	public JsonResult delete(String ids) {
		List<Long> dels = ConvertUtil.str2longs(ids);
		userConsoleService.batchDelSysUser(dels);
		return JsonResult.success();
	}

	@PostMapping(MODEL + "/update.json")
	@Function("user.update")
	@ResponseBody
	public JsonResult update(@Validated(ValidateConfig.UPDATE.class) CoreUser user) {
		boolean success = userConsoleService.updateTemplate(user);
		if (success) {
			this.platformService.clearFunctionCache();
			return JsonResult.success();
		} else {
			return JsonResult.failMessage("保存失败！");
		}
	}

	@PostMapping(MODEL + "/add.json")
	@Function("user.add")
	@ResponseBody
	public JsonResult<Long> add(@Validated(ValidateConfig.ADD.class) CoreUser user) {
		if (!platformService.isAllowUserName(user.getCode())) {
			return JsonResult.failMessage("不允许的注册名字 " + user.getCode());
		}
		user.setCreateTime(new Date());
		userConsoleService.saveUser(user);
		return JsonResult.success(user.getId());
	}

	@PostMapping(MODEL + "/view.json")
	@ResponseBody
	@Function("user.query")
	public JsonResult<CoreUser> view(Long id) {
		CoreUser user = userConsoleService.queryById(id);
		return JsonResult.success(user);
	}

	@PostMapping(MODEL + "/list.json")
	@Function("user.query")
	@ResponseBody
	public JsonResult<PageQuery<CoreUser>> index(UserQuery condtion) {

		PageQuery<CoreUser> page = condtion.getPageQuery();
		userConsoleService.queryByCondtion(page);
		return JsonResult.success(page);
	}

	@PostMapping(MODEL + "/list/condition.json")
	@Function("user.query")
	@ResponseBody
	public JsonResult<List<Map<String, Object>>> indexCondtion() {
		List<Map<String, Object>> list = AnnotationUtil.getInstance().getAnnotations(Query.class, UserQuery.class);
		return JsonResult.success(list);
	}

	@PostMapping(MODEL + "/disable.json")
	@Function("user.disable")
	@ResponseBody
	public JsonResult disableUser(String ids) {

		List<Long> dels = ConvertUtil.str2longs(ids);

		userConsoleService.batchUpdateUserState(dels, GeneralStateEnum.DISABLE);
		for (Long id : dels) {
			CoreUser user = userConsoleService.queryById(id);
			this.platformService.restUserSession(user.getCode());
		}
		return JsonResult.success();

	}

	/**
	 * 启用用户操作
	 * 
	 * @return
	 */
	@PostMapping(MODEL + "/enable.json")
	@Function("user.enable")
	@ResponseBody
	public JsonResult enableUser(String ids) {

		List<Long> enables = ConvertUtil.str2longs(ids);
		userConsoleService.batchUpdateUserState(enables, GeneralStateEnum.ENABLE);
		return JsonResult.success();

	}

	/**
	 * 管理员重置用户密码
	 * 
	 * @return
	 */
	@PostMapping(MODEL + "/changePassword.json")
	@Function("user.reset")
	@ResponseBody
	public JsonResult changePassword(Long id, String password) {

		userConsoleService.resetPassword(id, password);
		return new JsonResult().success();
	}

	/**
	 * 用户所有授权角色列表
	 * 
	 * @param id
	 *            用户id
	 * @return
	 */
	@PostMapping(MODEL + "/role/list.json")
	@Function("user.role")
	@ResponseBody
	public JsonResult<List<CoreUserRole>> getRoleList(UserRoleQuery roleQuery) {
		List<CoreUserRole> list = userConsoleService.getUserRoles(roleQuery);
		return JsonResult.success(list);
	}

	/**
	 * 用户添加授权角色页
	 * 
	 * @return
	 */
	@PostMapping(MODEL + "/role/add.json")
	@Function("user.role")
	@ResponseBody
	public JsonResult saveUserRole(@Validated CoreUserRole userRole) {
		userRole.setCreateTime(new Date());
		this.userConsoleService.saveUserRole(userRole);
		this.platformService.clearFunctionCache();
		return JsonResult.success(userRole.getId());

	}

	/**
	 * 删除用户角色授权
	 * 
	 * @return
	 */
	@PostMapping(MODEL + "/role/delete.json")
	@Function("user.role")
	@ResponseBody
	public JsonResult delUserRole(String ids) {
		List<Long> dels = ConvertUtil.str2longs(ids);

		userConsoleService.deleteUserRoles(dels);
		this.platformService.clearFunctionCache();
		return JsonResult.success();
	}

}