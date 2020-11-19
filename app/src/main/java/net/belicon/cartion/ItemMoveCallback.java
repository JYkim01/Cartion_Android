package net.belicon.cartion;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemMoveCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter adapter;

    public ItemMoveCallback(ItemTouchHelperAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) { //이벤트의 방향 설정 //아이템 좌우 배치
        /*
        int flagDrag = ItemTouchHelper.START | ItemTouchHelper.END; //item drag
        int flagSwipe = ItemTouchHelper.UP | ItemTouchHelper.DOWN; //item swipe
        */

        // 아이템 상하 배치
        int flagDrag = ItemTouchHelper.UP | ItemTouchHelper.DOWN; //item drag
        int flagSwipe = ItemTouchHelper.START | ItemTouchHelper.END;//item swipe

//        return makeMovementFlags(flagDrag, flagSwipe); //drag & swipe 사용
        return makeMovementFlags(flagDrag, 0); //swipe 액션 중지
//        return makeMovementFlags(0, flagSwipe); //drag 액션 중지
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder fromHolder, @NonNull RecyclerView.ViewHolder targetHolder) {
        //adapter의 onItemMove 호출
        adapter.onItemMove(fromHolder.getAdapterPosition(), targetHolder.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        //adapter의 onItemDismiss 호출
        adapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true; //롱터치 입력허용
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false; //swipe 사용하려면 return true;
    }

    //interface
    public interface ItemTouchHelperAdapter {
        void onItemMove(int fromPos, int targetPos);

        void onItemDismiss(int pos); //swipe 사용하려면 주석 해제 }
    }
}
