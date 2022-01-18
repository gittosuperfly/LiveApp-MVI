package cn.cai.star.liveapp.base;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerAdapter.BaseViewHolder> {

    private final int itemId;
    protected List<T> dataList;//数据集合
    private Context context;//上下文
    private OnItemClickListener onItemClickListener;//单击事件
    private OnItemDataClickListener<T> onItemDataClickListener;//单击事件 返回数据

    protected BaseRecyclerAdapter(List<T> dataList, int itemId) {
        this.dataList = dataList;
        this.itemId = itemId;
        setHasStableIds(true);
    }

    @NotNull
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(itemId, parent, false);
        final BaseViewHolder baseViewHolder = new BaseViewHolder(view);
        view.setOnClickListener(v -> {
            if (onItemClickListener != null)
                onItemClickListener.onItemClickListener(v, baseViewHolder.getLayoutPosition());
            if (onItemDataClickListener != null)
                onItemDataClickListener.onItemClickListener(v, baseViewHolder.getLayoutPosition(), dataList.get(baseViewHolder.getLayoutPosition()));
        });
        return baseViewHolder;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerAdapter.BaseViewHolder holder, final int position) {
        T t = dataList.get(position);
        bindData(holder, position, t);
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    /**
     * 刷新数据
     */
    public void refresh(List<T> datas) {
        this.dataList = datas;
        notifyDataSetChanged();
    }

    /**
     * 添加数据
     */
    public void addData(List<T> datas) {
        this.dataList.addAll(datas);
        notifyDataSetChanged();
    }

    /**
     * 添加数据(局部刷新，局部刷新时必须充血getItemId方法，同时setHasStableIds(true))
     */
    public void addDataWithoutAnim(List<T> datas) {
        if (datas == null)
            return;
        int size = this.dataList.size();
        this.dataList.addAll(datas);
        notifyItemRangeChanged(size, datas.size());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 绑定数据
     *
     * @param holder   具体的viewHolder
     * @param position 对应的索引
     */
    protected abstract void bindData(BaseViewHolder holder, int position, T t);

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(View v, int position);
    }

    public void setOnItemDataClickListener(OnItemDataClickListener<T> onItemDataClickListener) {
        this.onItemDataClickListener = onItemDataClickListener;
    }

    public interface OnItemDataClickListener<T> {
        void onItemClickListener(View v, int position,T t);
    }

    /**
     * 封装ViewHolder ,子类可以直接使用
     */
    public static class BaseViewHolder extends RecyclerView.ViewHolder {

        private final Map<Integer, View> mViewMap;

        BaseViewHolder(View itemView) {
            super(itemView);
            mViewMap = new HashMap<>();
        }

        /**
         * 获取设置的view
         */
        public View getView(int id) {
            View view = mViewMap.get(id);
            if (view == null) {
                view = itemView.findViewById(id);
                mViewMap.put(id, view);
            }
            return view;
        }
    }
}

