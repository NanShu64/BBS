package com.liang.bbs.rest.controller;

import com.github.pagehelper.PageInfo;
import com.liang.bbs.article.facade.dto.LabelDTO;
import com.liang.bbs.article.facade.dto.LabelSearchDTO;
import com.liang.bbs.article.facade.server.LabelService;
import com.liang.bbs.rest.config.login.NoNeedLogin;
import com.liang.bbs.rest.config.swagger.ApiVersion;
import com.liang.bbs.rest.config.swagger.ApiVersionConstant;
import com.liang.bbs.rest.utils.FileLengthUtils;
import com.liang.nansheng.common.auth.UserContextUtils;
import com.liang.nansheng.common.auth.UserSsoDTO;
import com.liang.nansheng.common.enums.ResponseCode;
import com.liang.nansheng.common.utils.CommonUtils;
import com.liang.nansheng.common.web.basic.ResponseResult;
import com.liang.nansheng.common.web.exception.BusinessException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author maliangnansheng
 * @date 2022/4/6 14:28
 */
@Slf4j
@RestController//返回的对象数据转换为JSON格式 @Controller通常与Thymeleaf模板引擎结合使用
@RequestMapping("/bbs/label/")//http://localhost:8080/bbs/label @RequestMapping注解主要负责URL的路由映射。它可以添加在Controller类或者具体的方法上
@Api(tags = "标签接口")
public class LabelController {
    @Reference
    private LabelService labelService;

    @Autowired
    private FileLengthUtils fileLengthUtils;

    @PostMapping("create")
    @ApiOperation(value = "新增标签")
    @ApiVersion(group = ApiVersionConstant.V_300)
    public ResponseResult<Boolean> create(@RequestBody LabelDTO labelDTO) {
        UserSsoDTO currentUser = UserContextUtils.currentUser();
        return ResponseResult.success(labelService.create(labelDTO, currentUser));
    }

    @PostMapping("/uploadLabelLogo")
    @ApiOperation(value = "上传标签logo")
    @ApiVersion(group = ApiVersionConstant.V_300)//@RequestParam用了这个注解则MultipartFile logo是必须的参数
    public ResponseResult<String> uploadLabelLogo(@RequestParam(value = "logo", required = false) MultipartFile logo) throws IOException {
        if (fileLengthUtils.isFileNotTooBig(logo.getBytes())) {
            return ResponseResult.success(labelService.uploadLabelLogo(logo.getBytes(), logo.getOriginalFilename()));
        } else {
            throw BusinessException.build(ResponseCode.EXCEED_THE_MAX, "请上传不超过 " +
                    CommonUtils.byteConversion(fileLengthUtils.getFileMaxLength()) + " 的图片!");
        }
    }

    @PostMapping("update")
    @ApiOperation(value = "更新标签")
    @ApiVersion(group = ApiVersionConstant.V_300)
    public ResponseResult<Boolean> update(@RequestBody LabelDTO labelDTO) {
        UserSsoDTO currentUser = UserContextUtils.currentUser();
        // 调用service方法更新
        return ResponseResult.success(labelService.update(labelDTO, currentUser));
    }

    @NoNeedLogin
    @GetMapping("getList")
    @ApiOperation(value = "获取标签")
    @ApiVersion(group = ApiVersionConstant.V_300)
    public ResponseResult<PageInfo<LabelDTO>> getList(LabelSearchDTO labelSearchDTO) {
        return ResponseResult.success(labelService.getList(labelSearchDTO));
    }

    @PostMapping("delete/{id}")
    @ApiOperation(value = "删除标签")
    @ApiVersion(group = ApiVersionConstant.V_300)
    public ResponseResult<Boolean> delete(@PathVariable Integer id) {
        return ResponseResult.success(labelService.delete(id));
    }

}
