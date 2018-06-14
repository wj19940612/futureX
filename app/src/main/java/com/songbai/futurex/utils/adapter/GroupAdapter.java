package com.songbai.futurex.utils.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Modified by john on 2018/6/9
 * <p>
 * Description: 分组通用 adapter
 * <p>
 * APIs:
 */
public abstract class GroupAdapter<T extends GroupAdapter.Groupable> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Group<T>> mGroupList;

    protected static final int HEAD = 0;
    protected static final int ITEM = 1;

    public GroupAdapter() {
        mGroupList = new ArrayList<>();
    }

    public void setGroupableList(List<T> groupableList) {
        mGroupList = group(groupableList);
        notifyDataSetChanged();
    }

    protected List<Group<T>> group(List<T> list) {
        List<Group<T>> result = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return result;
        }

        for (int i = 0; i < list.size(); i++) {
            Groupable data = list.get(i);
            Group group = new Group(data.getGroupNameRes(), data.getGroupId());
            group.add(data);

            int index = i;
            for (int j = index + 1; j < list.size(); j++) {
                Groupable groupable = list.get(j);
                if (group.getGroupId() == groupable.getGroupId()) {
                    group.add(groupable);
                    i = j;
                } else {
                    break;
                }
            } // for(j)
            result.add(group);
        }
        return result;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (Group group : mGroupList) {
            count += group.getItemCount();
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        int firstIndex = 0;
        for (Group group: mGroupList) {
            int size = group.getItemCount();
            int index = position - firstIndex;
            if (index == 0) {
                return HEAD;
            }
            if (index < size) {
                return ITEM;
            }
            firstIndex += size;
        }
        return ITEM;
    }

    public Groupable getItem(int position) {
        int firstIndex = 0;
        for (Group group: mGroupList) {
            int size = group.getItemCount();
            int index = position - firstIndex;
            if (index < size) {
                return group.getItem(index);
            }
            firstIndex += size;
        }
        return null;
    }

    public interface Groupable {

        int getGroupNameRes();

        int getGroupId();
    }

    static class Group<T extends Groupable> implements Groupable {

        private int mGroupNameRes;
        private int mGroupId;
        private List<T> mList;

        public Group(int groupNameRes, int groupId) {
            this.mGroupNameRes = groupNameRes;
            this.mGroupId = groupId;
            this.mList = new ArrayList<>();
        }

        public void add(T item) {
            mList.add(item);
        }

        public int getItemCount() {
            return mList != null ? mList.size() + 1 : 0;
        }

        public Groupable getItem(int index) {
            return index == 0 ? this : mList.get(index - 1);
        }

        @Override
        public int getGroupNameRes() {
            return mGroupNameRes;
        }

        @Override
        public int getGroupId() {
            return mGroupId;
        }
    }
}
