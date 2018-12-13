package org.jeecf.manager.module.template.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecf.common.enums.SplitCharEnum;
import org.jeecf.common.exception.BusinessException;
import org.jeecf.common.lang.StringUtils;
import org.jeecf.common.model.Request;
import org.jeecf.common.model.Response;
import org.jeecf.manager.common.controller.AbstractController;
import org.jeecf.manager.common.enums.BusinessErrorEnum;
import org.jeecf.manager.common.enums.EnumUtils;
import org.jeecf.manager.common.utils.DownloadUtils;
import org.jeecf.manager.common.utils.GenUtils;
import org.jeecf.manager.common.utils.NamespaceUtils;
import org.jeecf.manager.common.utils.TemplateUtils;
import org.jeecf.manager.common.utils.UserUtils;
import org.jeecf.manager.gen.model.GenTemplateEntity;
import org.jeecf.manager.module.config.model.domain.SysNamespace;
import org.jeecf.manager.module.template.model.domain.GenTemplate;
import org.jeecf.manager.module.template.model.po.GenFieldColumnPO;
import org.jeecf.manager.module.template.model.po.GenFieldPO;
import org.jeecf.manager.module.template.model.po.GenTablePO;
import org.jeecf.manager.module.template.model.po.GenTemplatePO;
import org.jeecf.manager.module.template.model.query.GenFieldColumnQuery;
import org.jeecf.manager.module.template.model.query.GenFieldQuery;
import org.jeecf.manager.module.template.model.query.GenTableQuery;
import org.jeecf.manager.module.template.model.query.GenTemplateQuery;
import org.jeecf.manager.module.template.model.result.GenFieldColumnResult;
import org.jeecf.manager.module.template.model.result.GenFieldResult;
import org.jeecf.manager.module.template.model.result.GenTableResult;
import org.jeecf.manager.module.template.model.result.GenTemplateResult;
import org.jeecf.manager.module.template.model.schema.GenTemplateSchema;
import org.jeecf.manager.module.template.service.GenFieldColumnService;
import org.jeecf.manager.module.template.service.GenFieldService;
import org.jeecf.manager.module.template.service.GenTableService;
import org.jeecf.manager.module.template.service.GenTemplateService;
import org.jeecf.manager.module.userpower.model.domain.SysUser;
import org.jeecf.manager.validate.groups.Add;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 模版配置
 * 
 * @author GloryJian
 * @version 1.0
 */
@Controller
@RequestMapping(value = { "template/genTemplate" })
@Api(value = "genTemplate api", tags = { "模版配置接口" })
public class GenTemplateController extends AbstractController {

	@Autowired
	private GenTemplateService genTemplateService;

	@Autowired
	private GenFieldService genFieldService;

	@Autowired
	private GenFieldColumnService genFieldColumnService;

	@Autowired
	private GenTableService genTableService;

	@GetMapping(value = { "", "index" })
	@RequiresPermissions("template:genTemplate:view")
	@ApiOperation(value = "视图", notes = "查看模版配置视图")
	@Override
	public String index(ModelMap map) {
		return "module/template/genTemplate";
	}

	@PostMapping(value = { "list" })
	@ResponseBody
	@RequiresPermissions("template:genTemplate:view")
	@ApiOperation(value = "列表", notes = "查询模版配置列表")
	public Response<List<GenTemplateResult>> list(@RequestBody Request<GenTemplateQuery, GenTemplateSchema> request) {
		return genTemplateService.findPageByAuth(new GenTemplatePO(request));
	}

	@PostMapping(value = { "save" })
	@ResponseBody
	@RequiresPermissions("template:genTemplate:edit")
	@ApiOperation(value = "更新", notes = "更新模版配置数据")
	public Response<GenTemplateResult> save(@RequestBody @Validated({ Add.class }) GenTemplate genTemplate) {
		SysUser sysUser = UserUtils.getCurrentUser();
		SysNamespace sysNamespace = NamespaceUtils.getNamespace(sysUser.getId());
		if (genTemplate.isNewRecord()) {
			GenTemplateQuery query = new GenTemplateQuery();
			query.setName(genTemplate.getName());
			query.setSysNamespaceId(Integer.valueOf(sysNamespace.getId()));
			List<GenTemplateResult> genTemplateList = genTemplateService.findList(new GenTemplatePO(query)).getData();
			if (CollectionUtils.isNotEmpty(genTemplateList)) {
				throw new BusinessException(BusinessErrorEnum.DATA_EXIT);
			}
		}
		String[] paths = genTemplate.getFileBasePath().split(File.separator);
		TemplateUtils.unzip(paths[0], paths[1], sysNamespace.getName());
		String fileName = StringUtils.substringBeforeLast(paths[1], ".");
		genTemplate.setFileBasePath(paths[0]+ File.separator + fileName);
		return genTemplateService.saveByAuth(genTemplate);
	}

	@PostMapping(value = { "delete/{id}" })
	@ResponseBody
	@RequiresPermissions("template:genTemplate:edit")
	@ApiOperation(value = "删除", notes = "删除模版配置数据")
	public Response<Integer> delete(@PathVariable("id") String id) {

		GenTemplate genTemplate = genTemplateService.getByAuth(new GenTemplate(id)).getData();
		if (genTemplate != null) {
			String userId = UserUtils.getCurrentUserId();
			SysNamespace sysNamespace = NamespaceUtils.getNamespace(userId);
			if (sysNamespace != null) {
				Response<Integer> res = genTemplateService.deleteByAuth(new GenTemplate(id));
				if (res.getData() != 0) {
					String filePath = StringUtils.substringBeforeLast(genTemplate.getFileBasePath(),
							SplitCharEnum.SLASH.getName());
					TemplateUtils.delDownload(filePath, sysNamespace.getName());
					return res;
				}
			}

		}
		return new Response<Integer>();
	}

	@PostMapping(value = { "field" })
	@ResponseBody
	@RequiresPermissions("template:genTemplate:view")
	@ApiOperation(value = "查询", notes = "查询模版参数")
	public Response<List<GenFieldResult>> getField() {
		return genFieldService.findListByAuth(new GenFieldPO(new GenFieldQuery()));
	}

	@PostMapping(value = { "upload" })
	@ResponseBody
	@RequiresPermissions("template:genTemplate:edit")
	@ApiOperation(value = "上传", notes = "上传模版文件")
	public Response<String> upload(@RequestParam("file") MultipartFile file) {
		SysUser sysUser = UserUtils.getCurrentUser();
		SysNamespace sysNamespace = NamespaceUtils.getNamespace(sysUser.getId());
		return new Response<>(GenUtils.upload(file, sysNamespace));
	}

	@PostMapping(value = { "params/{genFieldId}" })
	@ResponseBody
	@RequiresPermissions("template:genTemplate:view")
	@ApiOperation(value = "参数", notes = "查询模版参数")
	public Response<List<GenFieldColumnResult>> params(@PathVariable("genFieldId") Integer genFieldId)
			throws IOException {
		GenFieldColumnQuery columns = new GenFieldColumnQuery();
		columns.setGenFieldId(genFieldId);
		return genFieldColumnService.findList(new GenFieldColumnPO(columns));
	}

	@PostMapping(value = { "updateFile" })
	@ResponseBody
	@RequiresPermissions("template:genTemplate:edit")
	@ApiOperation(value = "更新", notes = "更新模版文件")
	public Response<String> updateFile(@RequestParam("file") MultipartFile file, String fileBasePath) {
		SysUser sysUser = UserUtils.getCurrentUser();
		SysNamespace sysNamespace = NamespaceUtils.getNamespace(sysUser.getId());
		String result = GenUtils.upload(file, sysNamespace);
		if (StringUtils.isNotEmpty(result)) {
			fileBasePath = StringUtils.substringBeforeLast(fileBasePath, SplitCharEnum.SLASH.getName());
			TemplateUtils.delDownload(fileBasePath, sysNamespace.getName());
		}
		return new Response<>(result);
	}

	@PostMapping(value = { "gen" })
	@ResponseBody
	@RequiresPermissions("template:genTemplate:view")
	@ApiOperation(value = "代码生成", notes = "代码生成")
	public void gen(GenTemplateEntity entity, HttpServletResponse response) throws IOException {
		GenTemplate queryTemplate = new GenTemplate();
		queryTemplate.setId(String.valueOf(entity.getTemplateId()));
		GenTemplate genTemplate = genTemplateService.getByAuth(queryTemplate).getData();
		if (genTemplate != null) {
			SysUser sysUser = UserUtils.getCurrentUser();
			SysNamespace sysNamespace = NamespaceUtils.getNamespace(sysUser.getId());
			if (sysNamespace != null) {
				String sourcePath = TemplateUtils.getUnzipPath(genTemplate.getFileBasePath(), sysNamespace.getName());
				String outPath = GenUtils.build(entity.getParams(), entity.getTableId(), sourcePath,
						genTemplate.getLanguage(), sysNamespace);
				DownloadUtils.downloadFile(response, outPath);
			}
		}
		return;
	}

	@PostMapping(value = { "download/{id}" })
	@ResponseBody
	@RequiresPermissions("template:genTemplate:view")
	@ApiOperation(value = "模版文件下载", notes = "下载")
	public void download(@PathVariable("id") String id, HttpServletResponse response) throws IOException {
		GenTemplate genTemplate = genTemplateService.get(new GenTemplate(id)).getData();
		if (genTemplate != null) {
			SysUser sysUser = UserUtils.getCurrentUser();
			SysNamespace sysNamespace = NamespaceUtils.getNamespace(sysUser.getId());
			if (sysNamespace != null) {
				String zipFilePath = TemplateUtils.getZipFilePath(genTemplate.getFileBasePath(),
						sysNamespace.getName());
				DownloadUtils.downloadFile(response, zipFilePath);
			}
		}
		return;

	}

	@PostMapping(value = { "queryTableList" })
	@ResponseBody
	@RequiresPermissions("template:genTemplate:view")
	@ApiOperation(value = "列表", notes = "查询代码生成业务表列表")
	public Response<List<GenTableResult>> findTable() {
		return genTableService.findListByAuth(new GenTablePO(new GenTableQuery()));
	}

	@PostMapping(value = { "getLanguages" })
	@ResponseBody
	@RequiresPermissions("template:genTemplate:view")
	@ApiOperation(value = "列表", notes = "查询代码生成业务表列表")
	public Response<String> getLanguages() {
		return new Response<>(EnumUtils.Language.toJsonString());
	}

}