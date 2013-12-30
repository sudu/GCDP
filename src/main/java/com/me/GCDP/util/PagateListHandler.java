package com.me.GCDP.util;

import java.util.List;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huweiqi
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-2-9              huweiqi               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class PagateListHandler<T> {

	//总记录数
	private int totalRowNum;
	
	//总页数
	private int totalPageNum;

	//当前页数
	private int pageNo = 1;

	//实体列表
	private List<T> myList;

	//起始行号
	private int startRow;

	//结束行号
	private int endRow;

	//每页记录数，默认为20
	private int pageSize = 20;

	//是否在最后一页后
	private boolean exceedLastPageNO;

	//是否在第一页前
	private boolean underFirstPageNO;

	public PagateListHandler() {
	}

	public boolean isExceedLastPageNO() {
		return exceedLastPageNO;
	}

	public boolean isUnderFirstPageNO() {
		return underFirstPageNO;
	}

	public int getTotalRowNum() {
		return totalRowNum;
	}

	public void setTotalRowNum(int totalRowNum) {
		this.totalRowNum = totalRowNum;
	}

	public int getTotalPageNum() {
		if(totalRowNum%pageSize==0)
			totalPageNum = totalRowNum/pageSize;
		else
			totalPageNum = totalRowNum/pageSize + 1;
		return totalPageNum;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public List<T> getMyList() {
		return myList;
	}

	public void setMyList(List<T> myList) {
		this.myList = myList;
	}

	public int getStartRow() {
		startRow = (this.pageNo-1)*this.pageSize + 1;
		return startRow;
	}

	public int getEndRow() {
		endRow = this.pageNo*this.pageSize;
		if(endRow > this.totalRowNum){
			endRow = this.totalRowNum;
		}
		return endRow;
	}

	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setExceedLastPageNO(boolean exceedLastPageNO) {
		this.exceedLastPageNO = exceedLastPageNO;
	}

	public void setUnderFirstPageNO(boolean underFirstPageNO) {
		this.underFirstPageNO = underFirstPageNO;
	}
	
	public static void main(String[] args){
		/*PagateListHandler p = new PagateListHandler();
		p.setTotalRowNum(98);
		p.setPageNo(5);
		System.out.println(p.getTotalPageNum());
		System.out.println(p.getStartRow());
		System.out.println(p.getEndRow());*/
	}

}