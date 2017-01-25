/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

package ir.irani.telecam.ui.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import ir.irani.telecam.Pouya.Helper.DialogFilter;
import ir.irani.telecam.Pouya.Setting.HiddenController;
import ir.irani.telecam.messenger.AndroidUtilities;
import ir.irani.telecam.messenger.ApplicationLoader;
import ir.irani.telecam.messenger.MessagesController;
import ir.irani.telecam.messenger.support.widget.RecyclerView;
import ir.irani.telecam.tgnet.TLRPC;
import ir.irani.telecam.ui.Cells.DialogCell;
import ir.irani.telecam.ui.Cells.LoadingCell;

import java.util.ArrayList;

public class DialogsAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private int dialogsType;
    private static DialogsAdapter instance;
    public boolean Hiddenmode;
    public static final String UNREAD  = "unread";
    public static final String FAVOR  = "favor";
    public static final String BOT  = "bot";
    public static final String CHANNEL  = "channel";
    public static final String ALL  = "all";
    public static final String SUPPERGROUP  = "sgroup";
    public static final String CONTACT  = "contact";
    public static final String GROUP  = "ngroup";
    public DialogsAdapter(){
        instance=this;
    }
    public static  DialogsAdapter getInstance(){
        if(instance!=null)return instance;
        return new DialogsAdapter(ApplicationLoader.applicationContext,0);
    }
    public static void RemoveInstans(){
        instance=null;
    }
    public int getDialogsType() {
        return dialogsType;
    }

    public void setDialogsType(int dialogsType) {
        this.dialogsType = dialogsType;
    }

    public static String categoryId;
    private long openedDialogId;
    private int currentCount;
    private ArrayList<TLRPC.TL_dialog> dialogsArray;

    public void setdialogstonull() {
        dialogsArray=null;
    }

    private class Holder extends RecyclerView.ViewHolder {

        public Holder(View itemView) {
            super(itemView);
        }
    }

    public DialogsAdapter(Context context, int type) {
        mContext = context;
        dialogsType = type;
    }

    public void setOpenedDialogId(long id) {
        openedDialogId = id;
    }

    public boolean isDataSetChanged() {
        int current = currentCount;
        return current != getItemCount() || current == 1;
    }
    public ArrayList<TLRPC.TL_dialog> getDialogsArray() {
        MessagesController.getInstance().loadingDialogs=false;
        ArrayList<TLRPC.TL_dialog> ret=new  ArrayList<>();
       // Log.e("Loading dialogs","type:>"+dialogsType+" cat:>"+ categoryId);
        if(dialogsType==0) {
            switch (categoryId) {
                case FAVOR:
                    ret= MessagesController.getInstance().dialogsFavoriteOnly;
                    break;
                case BOT:
                    ret= MessagesController.getInstance().dialogsBotOnly;
                    break;
                case UNREAD:
                    ret= MessagesController.getInstance().dialogsUnreadOnly;
                    break;
                case CHANNEL:
                    ret= MessagesController.getInstance().dialogsChannelOnly;
                    break;
                case SUPPERGROUP:
                    ret= MessagesController.getInstance().dialogsSuperGroupsOnly;
                    break;
                case GROUP:
                    ret= MessagesController.getInstance().dialogsJustGroupsOnly;
                    break;
                case CONTACT:
                    ret= MessagesController.getInstance().dialogsContactOnly;
                    break;
                case ALL:
//                    ret= MessagesController.getInstance().dialogsJustSecretChatOnly;
//                    break;
//                case 8:
                    ret= MessagesController.getInstance().dialogs;
                    break;
            }
        } else if (dialogsType == 1) {
            ret= MessagesController.getInstance().dialogsServerOnly;
        } else if (dialogsType == 2) {
            ret= MessagesController.getInstance().dialogsGroupsOnly;
        }
       // Log.e("run with hide dialogs","size:>"+ret.size());
        return HidePRoccess(ret);
    }

    private ArrayList<TLRPC.TL_dialog> HidePRoccess(ArrayList<TLRPC.TL_dialog> ret) {
        ArrayList<TLRPC.TL_dialog> reth=new ArrayList<>();
        for(int i=0;i<ret.size();i++){
            if(Hiddenmode){
               if(HiddenController.isHidden(ret.get(i).id)) reth.add(ret.get(i));
            }else{
                if(!HiddenController.isHidden(ret.get(i).id))reth.add(ret.get(i));
            }

        }
      //  Log.e("return hide dialogs","size:>"+reth.size());
        return reth;
    }

    @Override
    public int getItemCount() {
        int count = getDialogsArray().size();
        if (count == 0 && false) {
            return 0;
        }
        if (!MessagesController.getInstance().dialogsEndReached) {
            count++;
        }
        currentCount = count;
        return count;
    }

    public TLRPC.TL_dialog getItem(int i) {
        ArrayList<TLRPC.TL_dialog> arrayList = getDialogsArray();
        if (i < 0 || i >= arrayList.size()) {
            return null;
        }
        return arrayList.get(i);

    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (holder.itemView instanceof DialogCell) {
            ((DialogCell) holder.itemView).checkCurrentDialogIndex();
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = null;
        if (viewType == 0) {
            view = new DialogCell(mContext);

        } else if (viewType == 1) {
            view = new View(mContext);
        }
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder.getItemViewType() == 0) {
            DialogCell cell = (DialogCell) viewHolder.itemView;
            cell.setCategoryId(categoryId);
            cell.setHiddenmode(Hiddenmode);
            cell.useSeparator = (i != getItemCount() - 1);
            TLRPC.TL_dialog dialog = getItem(i);
            if (dialogsType == 0) {
                if (AndroidUtilities.isTablet()) {
                    cell.setDialogSelected(dialog.id == openedDialogId);
                }
            }
            cell.setDialog(dialog, i, dialogsType,Hiddenmode);
        }
    }

    @Override
    public int getItemViewType(int i) {
        if (i == getDialogsArray().size()) {
            return 1;
        }
        return 0;
    }
}
