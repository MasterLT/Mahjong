package com.wasu.game.domain;

import com.google.common.collect.Lists;
import com.wasu.game.enums.CardType;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerST implements Serializable {
	private static final long serialVersionUID = 111255L;

	private int position;
	private long playerId;
	private Boolean state;//是否准备
	private int status;//用户状态 听  胡 普通
	private List<Card> cards;
	private HuInfo huInfo;//要赢的牌和类型
	private Map<Integer,HuInfo> tempHuInfo;//临时保存
	private List<ChiOrPengTingInfo> chiOrPengTingInfos;
	private boolean stand;
	private Integer circleAfterTing;//如果听了&&此字段为null  说明不是听的当圈点炮 否则按未听处理
	private boolean online;

	public boolean isStand() {
		return stand;
	}

	public void setStand(boolean stand) {
		this.stand = stand;
	}

	public List<ChiOrPengTingInfo> getChiOrPengTingInfos() {
		return chiOrPengTingInfos;
	}

	public void setChiOrPengTingInfos(List<ChiOrPengTingInfo> chiOrPengTingInfos) {
		this.chiOrPengTingInfos = chiOrPengTingInfos;
	}

	public Map<Integer, HuInfo> getTempHuInfo() {
		return tempHuInfo;
	}

	public void setTempHuInfo(Map<Integer, HuInfo> tempHuInfo) {
		this.tempHuInfo = tempHuInfo;
	}

	public HuInfo getHuInfo() {
		return huInfo;
	}

	public void setHuInfo(HuInfo huInfo) {
		this.huInfo = huInfo;
	}
	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}
    
	public	PlayerST(){}
	public	PlayerST(long id){
		this.playerId=id;
		this.state=false;
		this.online=true;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getPosition() {
		return position;
	}

	public Integer getCircleAfterTing() {
		return circleAfterTing;
	}

	public void setCircleAfterTing(Integer circleAfterTing) {
		this.circleAfterTing = circleAfterTing;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public long getPlayerId() {
		return playerId;
	}
	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}
	public Boolean getState() {
		return state;
	}
	public void setState(Boolean state) {
		this.state = state;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	/**
	 * 增加一个牌
	 * @param cardNum
	 * @param type
	 */
	public void  addCard(int cardNum,int type) {
		boolean isExit=false;
		for (int i = 0; i < cards.size(); i++) {
			if (cards.get(i).getType()==type) {
				isExit=true;
				cards.get(i).addCard(cardNum);
			}
		}
		if (!isExit) {
			List<Integer> cardList=new ArrayList<Integer>();
			cardList.add(cardNum);
			Card card=new Card(type, cardList, cardList.size());
			cards.add(card);
		}
	}


	/**
	 * 根据玩家的牌 找到一种类型的牌
	 *
	 * @param
	 * @param type
	 * @return
	 */
	public  List<Integer> getCardsByType( int type) {
		List<Integer> cardList = new ArrayList<Integer>();
		if (CollectionUtils.isEmpty(cards))
			return cardList;
		for (int j = 0; j < cards.size(); j++) {
			if (cards.get(j).getType() != type) {
				continue;
			}
			cardList = cards.get(j).getCards();
			break;
		}
		return cardList;
	}


	public List<List<Integer>> getCardsListByType(int type){
		List<List<Integer>> cardsList=Lists.newArrayList();
		for (int j = 0; j < cards.size(); j++) {
			List<Integer> cards1=Lists.newArrayList();
			if (cards.get(j).getType()== type) {
				cards1 = cards.get(j).getCards();
				cardsList.add(cards1);
			}
		}
		return cardsList;
	}
	/**
	 * 增加几个牌
	 * @param
	 * @param type
	 */
	public void  addCard(List<Integer> cardList,int type) {
		boolean isExit=false;
		if (this.cards==null){
			List<Integer> cardList2=Lists.newArrayList(cardList);
			Card card=new Card(type, cardList2, cardList2.size());
			this.cards=Lists.newArrayList(card);
		}else {
			for (int i = 0; i < cards.size(); i++) {
				if (cards.get(i).getType()==type) {
					isExit=true;
					cards.get(i).addCard(cardList);
				}
			}
			if (!isExit) {
				List<Integer> cardList1=new ArrayList<Integer>();
				cardList1.addAll(cardList);
				Card card=new Card(type, cardList1, cardList1.size());
				cards.add(card);
			}
		}
	}
	/**
	 * 删掉一个牌
	 * @param cardNum
	 * @param type
	 */
	public void deleteCard(int cardNum,int type) {
		for (int i = 0; i < cards.size(); i++) {
			if (cards.get(i).getType()==type&&cards.get(i).getCards()!=null&&cards.get(i).getCards().size()>0) {
				if (type==CardType.YICHUPAI.getType()) {//如果是打出的牌 从后打出的牌开始删
					cards.get(i).removeLast();
				}else {
					cards.get(i).remove(cardNum);
				}
			}
		}
		
	}


	
	public List<List<Integer>> returnUseCards(){
		List<List<Integer>> useCards= Lists.newArrayList();
		List<Integer> shouPai=getCardsByType(CardType.SHOUPAI.getType());
		List<Integer> pengPai=getCardsByType(CardType.PENGPAI.getType());
		List<Integer> chiPai=getCardsByType(CardType.CHIPAI.getType());
		List<Integer> mingGang=getCardsByType(CardType.MINGGANG.getType());
		List<Integer> anGang=getCardsByType(CardType.ANGANG.getType());
		if (shouPai!=null&&shouPai.size()>0){
			useCards.add(shouPai);
		}
		if (pengPai!=null&&pengPai.size()>0){
			useCards.add(pengPai);
		}
		if (chiPai!=null&&chiPai.size()>0){
			useCards.add(chiPai);
		}
		if (mingGang!=null&&mingGang.size()>0){
			useCards.add(mingGang);
		}
		if (anGang!=null&&anGang.size()>0){
			useCards.add(anGang);
		}
		return useCards;
	}
	public void updateCards(List<Integer> cardList,int type){
		boolean isExit=false;
		if (this.cards==null&& !CollectionUtils.isEmpty(cardList)){
			List<Integer> cardList2=Lists.newArrayList(cardList);
			Card card=new Card(type, cardList2, cardList2.size());
			this.cards=Lists.newArrayList(card);
		}else {
			for (int i = 0; i < cards.size(); i++) {
				if (this.cards.get(i).getType()==type) {
					isExit=true;
					this.cards.get(i).setCards(cardList);
				}
			}
			if (!isExit) {
				List<Integer> cardList1=new ArrayList<Integer>();
				cardList1.addAll(cardList);
				Card card=new Card(type, cardList1, cardList1.size());
				cards.add(card);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		for(Card c:cards){
			if(c.getType()==CardType.SHOUPAI.getType())
				sb.append("手牌:").append(c.getCards()).append(";");
			else if(c.getType()==CardType.CHIPAI.getType())
				sb.append("吃牌:").append(c.getCards()).append(";");
			else if(c.getType()==CardType.PENGPAI.getType())
				sb.append("碰牌:").append(c.getCards()).append(";");
			else if(c.getType()==CardType.MINGGANG.getType())
				sb.append("明杠牌:").append(c.getCards()).append(";");
			else if(c.getType()==CardType.ANGANG.getType())
				sb.append("暗杠牌:").append(c.getCards()).append(";");
			else if(c.getType()==CardType.YICHUPAI.getType())
				sb.append("已出牌:").append(c.getCards()).append(";");
		}
		return "PlayerST{" +
				"position=" + position +
				", playerId=" + playerId +
				", cards=" + sb.toString() +
				", huInfo=" + huInfo +
				", tempHuInfo=" + tempHuInfo +
				", chiOrPengTingInfos=" + chiOrPengTingInfos +
				'}';
	}

	public static void main(String[] args){

     }
}
