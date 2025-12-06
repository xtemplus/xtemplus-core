package io.github.xtemplus.core.enums;

import io.github.xtemplus.core.common.BaseErrEnum;

/**
 * 文件操作错误枚举 (470-489)
 */
public enum FileErrEnum implements BaseErrEnum {
    // 文件上传 (470-479)
    FILE_UPLOAD_FAILED(470, "文件上传失败"),
    FILE_EMPTY(471, "上传文件为空"),
    FILE_SIZE_EXCEED(472, "文件大小超过限制"),
    FILE_TYPE_NOT_ALLOW(473, "文件类型不允许"),
    FILE_NAME_INVALID(474, "文件名不合法"),
    FILE_PATH_INVALID(475, "文件路径不合法"),
    
    // 文件操作 (480-489)
    FILE_NOT_EXIST(480, "文件不存在"),
    FILE_READ_ERROR(481, "文件读取失败"),
    FILE_WRITE_ERROR(482, "文件写入失败"),
    FILE_DELETE_ERROR(483, "文件删除失败"),
    FILE_COPY_ERROR(484, "文件复制失败"),
    FILE_MOVE_ERROR(485, "文件移动失败"),
    IMAGE_FORMAT_ERROR(486, "图片格式错误"),
    STORAGE_SPACE_INSUFFICIENT(487, "存储空间不足"),
    FILE_PERMISSION_DENIED(488, "文件权限不足"),
    FILE_ENCRYPT_ERROR(489, "文件加密失败"),
    ;
    
    private final Integer errCode;
    private final String errMsg;
    
    FileErrEnum(Integer errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
    
    @Override
    public Integer errCode() {
        return this.errCode;
    }
    
    @Override
    public String errMsg() {
        return this.errMsg;
    }
}