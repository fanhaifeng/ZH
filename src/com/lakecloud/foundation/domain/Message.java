package com.lakecloud.foundation.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.lakecloud.core.constant.Globals;
import com.lakecloud.core.domain.IdEntity;

/**
 * @info 站内短信类，用户之间可以根据用户名发送站内短信息
 * @since V1.0
 * @author 江苏太湖云计算信息技术股份有限公司 www.chinacloud.net erikchang
 * 
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "message")
public class Message extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private User fromUser;// 发送用户
	@ManyToOne(fetch = FetchType.LAZY)
	private User toUser;// 接收用户
	private int status;// 短信状态,0是未读，1为已读
	@Column(columnDefinition = "int default 0")
	private int reply_status;// 短信回复状态，0为没有回复，1为有新回复
	private String title;// 短信标题
	/**
	 * @info 数据库移植兼容性修改
	 * @author chenyong
	 * @see
	 * 	修改@Column(columnDefinition = "LongText")
	 */
	@Lob
	@Column(length = 1048576)
	private String content;// 短信内容
	@ManyToOne(fetch = FetchType.LAZY)
	private Message parent;
	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
	List<Message> replys = new ArrayList<Message>();// 短信回复，和parent进行映射
	private int type;// 短信类型，0为系统短信，1为用户之间的短信

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public User getFromUser() {
		return fromUser;
	}

	public void setFromUser(User fromUser) {
		this.fromUser = fromUser;
	}

	public User getToUser() {
		return toUser;
	}

	public void setToUser(User toUser) {
		this.toUser = toUser;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Message getParent() {
		return parent;
	}

	public void setParent(Message parent) {
		this.parent = parent;
	}

	public List<Message> getReplys() {
		return replys;
	}

	public void setReplys(List<Message> replys) {
		this.replys = replys;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getReply_status() {
		return reply_status;
	}

	public void setReply_status(int reply_status) {
		this.reply_status = reply_status;
	}
}
