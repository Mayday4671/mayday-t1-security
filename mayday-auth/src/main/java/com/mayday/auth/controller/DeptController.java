package com.mayday.auth.controller;

import com.mayday.auth.common.R;
import com.mayday.auth.model.vo.DeptTreeVo;
import com.mayday.auth.service.SysDeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 部门控制器
 *
 * @author MayDay Auth Generator
 * @since 1.0.0
 */
@RestController
@RequestMapping("/dept")
@RequiredArgsConstructor
public class DeptController {

    private final SysDeptService deptService;

    /**
     * 获取部门树形结构
     * <p>
     * 用于用户编辑时的部门多选组件 (TreeSelect)
     * </p>
     *
     * @return 部门树
     */
    @GetMapping("/tree")
    public R<List<DeptTreeVo>> getDeptTree() {
        List<DeptTreeVo> tree = deptService.getDeptTree();
        return R.ok(tree);
    }
}
