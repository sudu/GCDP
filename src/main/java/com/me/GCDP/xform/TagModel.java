package com.me.GCDP.xform;

public class TagModel {
	private String tagid="";
	public String getTagid() {
		return tagid;
	}
	public void setTagid(String tagid) {
		this.tagid = tagid;
	}
	private String editurl="";
	public String getEditurl() {
		return editurl;
	}
	public void setEditurl(String editurl) {
		this.editurl = editurl;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	private int type=0;
	private String content="";
	private String title="";
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	private String parms;
	public String getParms() {
		return parms;
	}
	public void setParms(String parms) {
		this.parms = parms;
	}
}
