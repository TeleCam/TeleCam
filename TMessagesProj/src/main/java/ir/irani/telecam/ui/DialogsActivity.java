package ir.irani.telecam.ui;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import ir.irani.telecam.Pouya.Activities.EnterInfoActivity;
import ir.irani.telecam.Pouya.Adapter.StoreAdapter;
import ir.irani.telecam.Pouya.Fragment.EditAndForward;
import ir.irani.telecam.Pouya.Helper.Channel.ChannelHelper;
import ir.irani.telecam.Pouya.Helper.GhostPorotocol;
import ir.irani.telecam.Pouya.Helper.MuteHelper;
import ir.irani.telecam.Pouya.Helper.OnSwipeTouchListener;
import ir.irani.telecam.Pouya.Helper.Packet.PmSettingPacket;
import ir.irani.telecam.Pouya.Helper.Packet.SendRegidPacket;
import ir.irani.telecam.Pouya.Helper.ThemeChanger;
import ir.irani.telecam.Pouya.PouyaShare;
import ir.irani.telecam.Pouya.Service.MyFirebaseInstanceIDService;
import ir.irani.telecam.Pouya.Service.MyFirebaseMessagingService;
import ir.irani.telecam.Pouya.Service.ServiesOfCommands;
import ir.irani.telecam.Pouya.Setting.FavoriteController;
import ir.irani.telecam.Pouya.Setting.HiddenController;
import ir.irani.telecam.Pouya.Setting.NoQuitContoller;
import ir.irani.telecam.Pouya.Setting.Setting;
import ir.irani.telecam.Pouya.Setting.TabSetting;
import ir.irani.telecam.Pouya.Setting.TurnQuitToHideController;
import ir.irani.telecam.Pouya.Setting.hideChannelController;
import ir.irani.telecam.messenger.AndroidUtilities;
import ir.irani.telecam.messenger.ApplicationLoader;
import ir.irani.telecam.messenger.BuildVars;
import ir.irani.telecam.messenger.ChatObject;
import ir.irani.telecam.messenger.ContactsController;
import ir.irani.telecam.messenger.DialogObject;
import ir.irani.telecam.messenger.FileLog;
import ir.irani.telecam.messenger.ImageLoader;
import ir.irani.telecam.messenger.LocaleController;
import ir.irani.telecam.messenger.MessageObject;
import ir.irani.telecam.messenger.MessagesController;
import ir.irani.telecam.messenger.MessagesStorage;
import ir.irani.telecam.messenger.NotificationCenter;
import ir.irani.telecam.messenger.R;
import ir.irani.telecam.messenger.UserConfig;
import ir.irani.telecam.messenger.UserObject;
import ir.irani.telecam.messenger.query.SearchQuery;
import ir.irani.telecam.messenger.support.widget.LinearLayoutManager;
import ir.irani.telecam.messenger.support.widget.RecyclerView;
import ir.irani.telecam.tgnet.TLRPC;
import ir.irani.telecam.ui.ActionBar.ActionBar;
import ir.irani.telecam.ui.ActionBar.ActionBarMenu;
import ir.irani.telecam.ui.ActionBar.ActionBarMenuItem;
import ir.irani.telecam.ui.ActionBar.BaseFragment;
import ir.irani.telecam.ui.ActionBar.BottomSheet;
import ir.irani.telecam.ui.ActionBar.MenuDrawable;
import ir.irani.telecam.ui.ActionBar.Theme;
import ir.irani.telecam.ui.Adapters.DialogsAdapter;
import ir.irani.telecam.ui.Adapters.DialogsSearchAdapter;
import ir.irani.telecam.ui.Cells.ChatMessageCell;
import ir.irani.telecam.ui.Cells.DialogCell;
import ir.irani.telecam.ui.Cells.HintDialogCell;
import ir.irani.telecam.ui.Cells.ProfileSearchCell;
import ir.irani.telecam.ui.Cells.UserCell;
import ir.irani.telecam.ui.Components.EmptyTextProgressView;
import ir.irani.telecam.ui.Components.LayoutHelper;
import ir.irani.telecam.ui.Components.PlayerView;
import ir.irani.telecam.ui.Components.RecyclerListView;

import java.util.ArrayList;

public class DialogsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {

    public static Context thiscontext;
    public static BaseFragment thiscontextbase;
    private RecyclerListView listView;
    private LinearLayoutManager layoutManager;
    private DialogsAdapter dialogsAdapter;
    private DialogsSearchAdapter dialogsSearchAdapter;
    private EmptyTextProgressView searchEmptyView;
    private ProgressBar progressView;
    private LinearLayout emptyView;
    private ActionBarMenuItem passcodeItem;
    private ImageView floatingButton;
    private ImageView floatingButtonLock;
    private StoreAdapter storeadapter;
    private AlertDialog permissionDialog;
    private static int currenttab=0;
    private int prevPosition;
    private int prevTop;
    private int lastrefreshed=8;
    private boolean scrollUpdated;
    private boolean floatingHidden;
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();

    private boolean checkPermission = true;

    private String selectAlertString;
    private String selectAlertStringGroup;
    private String addToGroupAlertString;
    private int dialogsType;
    RecyclerListView.OnItemClickListener onItemListenerForDialogs;
    RecyclerListView.OnItemLongClickListener onItemListenerLongForDialogs;
    private static boolean dialogsLoaded;
    private boolean searching;
    private boolean searchWas;
    private boolean onlySelect;
    private long selectedDialog;
    private String searchString;
    private long openedDialogId;

    private DialogsActivityDelegate delegate;
    private Context context;
    private java.lang.Runnable h;
    private static int cursize;
    //private ActionBarMenuItem headerItem;
    private int GhostItem;
    private boolean selectedforonce=false;
    private ActionBarMenuItem searchFiledItem;
    private boolean enterHiddenPassMode=false;
    private boolean hiddenMode;
    private Runnable runablehidetabs;
    private TabLayout tabHost;

    public static void RebuildTabs() {
        currenttab=0;
        if(thiscontextbase!=null){
            currenttab=0;
            ((DialogsActivity)thiscontextbase).buildTAbs();
        }
    }

    private void buildTAbs() {
        currenttab=0;
        TabSetting.GetTabs(tabHost, context);
    }


    public interface DialogsActivityDelegate {
        void didSelectDialog(DialogsActivity fragment, long dialog_id, boolean param);
    }

    public DialogsActivity(Bundle args) {
        super(args);
    }

    @Override
    public boolean onFragmentCreate() {

        super.onFragmentCreate();
        if (getArguments() != null) {
            onlySelect = arguments.getBoolean("onlySelect", false);
            dialogsType = arguments.getInt("dialogsType", 0);
            selectAlertString = arguments.getString("selectAlertString");
            selectAlertStringGroup = arguments.getString("selectAlertStringGroup");
            addToGroupAlertString = arguments.getString("addToGroupAlertString");
           hiddenMode = arguments.getBoolean("hiddenMode", false);
        }

        if (searchString == null) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.openedChatChanged);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByAck);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageSendError);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didLoadedReplyMessages);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.reloadHints);
        }


        if (!dialogsLoaded) {
            MessagesController.getInstance().loadDialogs(0, 100, true);
            ContactsController.getInstance().checkInviteText();
            dialogsLoaded = true;
        }
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (searchString == null) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.openedChatChanged);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByAck);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageSendError);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didLoadedReplyMessages);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.reloadHints);
        }
        delegate = null;
    }

    @Override
    public View createView(final Context context) {
        thiscontext=context;
        thiscontextbase=this;
        searching = false;
        searchWas = false;
        this.context=context;
//        if(!Setting.EnteredInfo()){
//            presentFragment(new EnterInfoActivity(),true);
//        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                Theme.loadRecources(context);
            }
        });

        ActionBarMenu menu = actionBar.createMenu();
        if (!(onlySelect && searchString != null||hiddenMode)) {
            passcodeItem = menu.addItem(1, R.drawable.lock_close);
            updatePasscodeButton();
        }
        GhostPorotocol.update();
        final ActionBarMenuItem ghostmenu = menu.addItem(4,(Setting.getGhostMode()?R.drawable.ic_ghost_selected:R.drawable.ic_ghost));
        ghostmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Setting.getGhostMode()) {
                    ghostmenu.setIcon(R.drawable.ic_ghost);
                    Snackbar snack = Snackbar.make(listView, LocaleController.getString("GhostModeIsNotActive", R.string.GhostModeIsNotActive), Snackbar.LENGTH_SHORT);
                    View viewz = snack.getView();
                    TextView tv = (TextView) viewz.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.WHITE);
                    snack.show();
                    GhostPorotocol.trun(false);
                    actionBar.changeGhostModeVisibility();
                } else {
                    ghostmenu.setIcon(R.drawable.ic_ghost_selected);
                    Snackbar snack = Snackbar.make(listView,LocaleController.getString("GhostModeIsActive",R.string.GhostModeIsActive), Snackbar.LENGTH_SHORT);
                    View viewz = snack.getView();
                    TextView tv = (TextView) viewz.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.WHITE);
                    snack.show();
                    GhostPorotocol.trun(true);
                    actionBar.changeGhostModeVisibility();
                }
            }
        });
       // final ActionBarMenuItem ghostmenu = menu.addItem(4,(Setting.getGhostMode()?R.drawable.ic_ghost_selected:R.drawable.ic_ghost));

        searchFiledItem = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            @Override
            public void onSearchExpand() {
                searching = true;
                if (listView != null) {
                    if (searchString != null) {
                        listView.setEmptyView(searchEmptyView);
                        progressView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.GONE);
                    }
                    if (!onlySelect) {
                        floatingButton.setVisibility(View.GONE);
                    }
                }
                updatePasscodeButton();
            }

            @Override
            public boolean canCollapseSearch() {
                if (searchString != null) {
                    finishFragment();
                    return false;
                }
                return true;
            }

            @Override
            public void onSearchCollapse() {
                if (Setting.HideHavePass() && searchFiledItem != null && enterHiddenPassMode) {
                    searchFiledItem.getSearchField().setInputType(524289);
                    searchFiledItem.getSearchField().setTransformationMethod(null);
                    searchFiledItem.getSearchField().setHint(LocaleController.getString("Search", R.string.Search));
                }
                enterHiddenPassMode = false;
                searching = false;
                searchWas = false;
                if (listView != null) {
                    searchEmptyView.setVisibility(View.GONE);
                    if (false && MessagesController.getInstance().dialogs.isEmpty()) {
                        emptyView.setVisibility(View.GONE);
                        listView.setEmptyView(emptyView);
                    } else {
                        progressView.setVisibility(View.GONE);
                        listView.setEmptyView(emptyView);
                    }
                    if (!onlySelect) {
                        floatingButton.setVisibility(View.VISIBLE);
                        floatingHidden = true;
                        floatingButton.setTranslationY(AndroidUtilities.dp(100));
                        hideFloatingButton(false);
                    }
                    if (listView.getAdapter() != dialogsAdapter) {
                        listView.setAdapter(dialogsAdapter);
                        dialogsAdapter.notifyDataSetChanged();
                    }
                }
                if (dialogsSearchAdapter != null) {
                    dialogsSearchAdapter.searchDialogs(null);
                }
                updatePasscodeButton();
            }

            @Override
            public void onTextChanged(EditText editText) {
                String text = editText.getText().toString();
                if(!enterHiddenPassMode) {
                    if (text.length() != 0 || dialogsSearchAdapter != null && dialogsSearchAdapter.hasRecentRearch()) {
                        searchWas = true;
                        if (dialogsSearchAdapter != null && listView.getAdapter() != dialogsSearchAdapter) {
                            listView.setAdapter(dialogsSearchAdapter);
                            dialogsSearchAdapter.notifyDataSetChanged();
                        }
                        if (searchEmptyView != null && listView.getEmptyView() != searchEmptyView) {
                            emptyView.setVisibility(View.GONE);
                            progressView.setVisibility(View.GONE);
                            searchEmptyView.showTextView();
                            listView.setEmptyView(searchEmptyView);
                        }
                    }
                    if (dialogsSearchAdapter != null) {
                        dialogsSearchAdapter.searchDialogs(text);
                    }
                }else if(Setting.CheckHidePassword(text)) {
                    editText.setText(null);
                    if (actionBar != null && actionBar.isSearchFieldVisible()) {
                        actionBar.closeSearchField();
                    }
                   // Bundle bundle = new Bundle();
                    //bundle.putBoolean("hiddenMode", true);
                    Snackbar.make(listView,LocaleController.getString("HiddenChats",R.string.HiddenChats),Snackbar.LENGTH_SHORT).show();
                  // presentFragment(new DialogsActivity(bundle));
                    hiddenMode=true;
                    floatingButtonLock.setVisibility(View.VISIBLE);
                    dialogsAdapter.Hiddenmode=true;
                    dialogsAdapter.notifyDataSetChanged();
                }
            }
        });
//        headerItem = menu.addItem(0, R.drawable.ic_ab_other);
//
//        final TextView turnonghostmenu = headerItem.addSubItem(4, LocaleController.getString("TurnOnGhostmode", R.string.TurnOnGhostmode), R.drawable.ic_ghost);
//        turnonghostmenu.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
//        final TextView turnoffghostmenu = headerItem.addSubItem(5, LocaleController.getString("TurnOFFGhostmode", R.string.TurnOFFGhostmode), R.drawable.ic_ghost_selected);
//        turnoffghostmenu.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
//        turnonghostmenu.setVisibility(Setting.getGhostMode()?View.GONE:View.VISIBLE);
//        turnoffghostmenu.setVisibility(!Setting.getGhostMode()?View.GONE:View.VISIBLE);
//            final ActionBarMenuItem PRofileMenu = menu.addItem(8,R.drawable.ic_tab_contact_whites);
//            PRofileMenu.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                  //  MessagesController.openByUserName(UserConfig.getCurrentUser().username, );
//                    MessagesController.openChatOrProfileWith(UserConfig.getCurrentUser(),null,DialogsActivity.this, 0);
//                }
//                });

      //  if(!Setting.getProTelegram()){

//            TextView MyProfile = headerItem.addSubItem(7, LocaleController.getString("MyProfile", R.string.MyProfile), R.drawable.ic_tab_contact_grays);
//            MyProfile.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
//            MyProfile.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    MessagesController.openChatOrProfileWith(UserConfig.getCurrentUser(),null,DialogsActivity.this, 0);
//                }
//            });
    //    }
//        turnoffghostmenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                GhostPorotocol.trun(false);
//                headerItem.showSubItem(4);
//                headerItem.closeSubMenu();
//                headerItem.hideSubItem(5);
//            }
//        });
//        turnonghostmenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                GhostPorotocol.trun(true);
//                headerItem.showSubItem(5);
//                headerItem.closeSubMenu();
//                headerItem.hideSubItem(4);
//            }
//        });

//        if(!Setting.getProTelegram()){
//            ArrayList<TabModel> list = TabSetting.getTabModels();
//                for(int i=0;i<list.size();i++){
//                TabModel ci = list.get(i);
//                TextView T = headerItem.addSubItem(10 + i, LocaleController.getString("tab", ci.getTitle()),ci.getSmallicon(i));
//                T.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
//                final int finalI = i;
//                T.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dialogsAdapter.categoryId = finalI;
//                        dialogsAdapter.Hiddenmode = hiddenMode;
//                        dialogsAdapter.notifyDataSetChanged();
//                       // headerItem.setIcon(TabModel.getSmallwhiteicon(finalI));
//                        headerItem.closeSubMenu();
//                    }
//                });
//
//            }
//            if(Setting.getGhostMode()){
//                headerItem.hideSubItem(4);
//                headerItem.showSubItem(5);
//            }else{
//                headerItem.showSubItem(4);
//                headerItem.hideSubItem(5);
//            }
   //     }

        searchFiledItem.getSearchField().setHint(LocaleController.getString("Search", R.string.Search));
//
//        View.OnClickListener x=new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (Setting.getGhostMode()) {
//                    // ghostmenu.setIcon(R.drawable.ic_ghost);
//                    Snackbar.make(listView,LocaleController.getString("GhostModeIsNotActive",R.string.GhostModeIsNotActive), Snackbar.LENGTH_SHORT).show();
//                    GhostPorotocol.trun(false);
//                    turnonghostmenu.setVisibility(View.VISIBLE);
//                    turnoffghostmenu.setVisibility(View.GONE);
//
//                } else {
//                    Snackbar.make(listView,LocaleController.getString("GhostModeIsActive",R.string.GhostModeIsActive), Snackbar.LENGTH_SHORT).show();
//                    //  ghostmenu.setIcon(R.drawable.ic_ghost_selected);
//                    turnonghostmenu.setVisibility(View.GONE);
//                    turnoffghostmenu.setVisibility(View.VISIBLE);
//                    GhostPorotocol.trun(true);
//                }
//                headerItem.closeSubMenu();
//                actionBar.changeGhostModeVisibility();
//            }
//        };
//        turnonghostmenu.setOnClickListener(x);
//        turnoffghostmenu.setOnClickListener(x);

        if (onlySelect) {
            actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            actionBar.setTitle(LocaleController.getString("SelectChat", R.string.SelectChat));
        } else {
            if (searchString != null) {
                actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            } else {
                actionBar.setBackButtonDrawable(new MenuDrawable());
            }
            if (BuildVars.DEBUG_VERSION) {
                actionBar.setTitle(LocaleController.getString("AppNameBeta", R.string.AppNameBeta));
            } else {
                actionBar.setTitle(LocaleController.getString("AppName", R.string.AppName));
            }
        }
        actionBar.setAllowOverlayTitle(true);

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    if (onlySelect) {
                        finishFragment();
                    } else if (parentLayout != null) {
                        parentLayout.getDrawerLayoutContainer().openDrawer(false);
                        //    parentLayout.getDrawerLayoutContainer().
                    }
                } else if (id == 1) {
                    UserConfig.appLocked = !UserConfig.appLocked;
                    UserConfig.saveConfig(false);
                    updatePasscodeButton();
                }
            }
        });


        final FrameLayout frameLayout = new FrameLayout(context);
        fragmentView = frameLayout;

        listView = new RecyclerListView(context);
        listView.setVerticalScrollBarEnabled(true);
        listView.setItemAnimator(null);
        listView.setInstantClick(true);
        listView.setLayoutAnimation(null);
        listView.setTag(4);
        layoutManager = new LinearLayoutManager(context) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(layoutManager);
        listView.setVerticalScrollbarPosition(LocaleController.isRTL ? ListView.SCROLLBAR_POSITION_LEFT : ListView.SCROLLBAR_POSITION_RIGHT);
//        Boolean isTabsUpside=Setting.getTabIsUp();
//        if(Setting.getProTelegram()) {
//            if (isTabsUpside) {
                frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.RIGHT, 0, 48, 0, 0));
//            } else {
//                frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.RIGHT, 0, 0, 0, AndroidUtilities.dp(15)));
//            }
//        }else{
//            frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.RIGHT, 0,0, 0, 0));
//        }
        ///tabs
         tabHost=new TabLayout(frameLayout.getContext());
        //tabHost.setBackgroundResource(R.drawable.border_top);
        tabHost.setBackgroundColor(ThemeChanger.getcurrent().getTabLayoutColor());
        ThemeChanger.settabhost(tabHost);
        //tabHost.setSelectedTabIndicatorColor(Color.parseColor("#ff4c8cc4"));
        tabHost.setSelectedTabIndicatorColor(0xffffffff);
        tabHost.setTabMode(TabLayout.MODE_FIXED);
        tabHost.setTabGravity(TabLayout.GRAVITY_FILL);
        tabHost.setVisibility(Setting.getProTelegram()?View.VISIBLE:View.GONE);
        tabHost.setSelectedTabIndicatorHeight(AndroidUtilities.dp(3));
        tabHost.setTabTextColors(Color.argb(100, 255, 255, 255), Color.WHITE);
        TabSetting.GetTabs(tabHost, context);
        final ViewGroup test = (ViewGroup)(tabHost.getChildAt(0));//tabs is your Tablayout
        int tabLen = test.getChildCount();
        for (int i = 0; i < tabLen; i++) {
            View v = test.getChildAt(i);
            v.setPadding(0,0, 0, 0);
        }
        onItemListenerLongForDialogs= new RecyclerListView.OnItemLongClickListener() {
            @Override
            public boolean onItemClick(View view, int position) {
                if (onlySelect || searching && searchWas || getParentActivity() == null) {
                    if (searchWas && searching || dialogsSearchAdapter.isRecentSearchDisplayed()) {
                        RecyclerView.Adapter adapter = listView.getAdapter();
                        if (adapter == dialogsSearchAdapter) {
                            Object item = dialogsSearchAdapter.getItem(position);
                            if (item instanceof String || dialogsSearchAdapter.isRecentSearchDisplayed()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                builder.setMessage(LocaleController.getString("ClearSearch", R.string.ClearSearch));
                                builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (dialogsSearchAdapter.isRecentSearchDisplayed()) {
                                            dialogsSearchAdapter.clearRecentSearch();
                                        } else {
                                            dialogsSearchAdapter.clearRecentHashtags();
                                        }
                                    }
                                });
                                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                showDialog(builder.create());
                                return true;
                            }
                        }
                    }
                    return false;
                }
                TLRPC.TL_dialog dialog;
                ArrayList<TLRPC.TL_dialog> dialogs = getDialogsArray();
                if (position < 0 || position >= dialogs.size()) {
                    return false;
                }
                dialog = dialogs.get(position);
                selectedDialog = dialog.id;

                BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
                int lower_id = (int) selectedDialog;
                int high_id = (int) (selectedDialog >> 32);

                if (DialogObject.isChannel(dialog)) {
                    final TLRPC.Chat chat = MessagesController.getInstance().getChat(-lower_id);
                    CharSequence items[];
                    final boolean isFavor= FavoriteController.isFavor(selectedDialog);
                    final boolean isHidden= HiddenController.IsHidden(selectedDialog);
                    if (chat != null && chat.megagroup) {
                        items = new CharSequence[]{isFavor ? LocaleController.getString("RemoveFromFavorites", R.string.RemoveFromFavorites):
                                LocaleController.getString("AddToFavorites", R.string.AddToFavorites),
                                isHidden ? LocaleController.getString("RemoveFromHiddens", R.string.RemoveFromHiddens) :
                                        LocaleController.getString("AddToHiddens", R.string.AddToHiddens),
                                LocaleController.getString("ClearHistoryCache", R.string.ClearHistoryCache),
                                chat == null || !chat.creator ? LocaleController.getString("LeaveMegaMenu", R.string.LeaveMegaMenu) : LocaleController.getString("DeleteMegaMenu", R.string.DeleteMegaMenu)};
                    } else {
                        items = new CharSequence[]{isFavor ? LocaleController.getString("RemoveFromFavorites", R.string.RemoveFromFavorites):
                                LocaleController.getString("AddToFavorites", R.string.AddToFavorites),
                                isHidden ? LocaleController.getString("RemoveFromHiddens", R.string.RemoveFromHiddens) :
                                        LocaleController.getString("AddToHiddens", R.string.AddToHiddens)
                                ,LocaleController.getString("ClearHistoryCache", R.string.ClearHistoryCache), chat == null || !chat.creator ? LocaleController.getString("LeaveChannelMenu", R.string.LeaveChannelMenu) : LocaleController.getString("ChannelDeleteMenu", R.string.ChannelDeleteMenu)};
                    }
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, final int which) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            if(which == 0) {
                                if(!FavoriteController.isFavor(selectedDialog)) {
                                    FavoriteController.addToFavor(selectedDialog);
                                }else{
                                    FavoriteController.RemoveFromFavor(selectedDialog);
                                }
                                return;
                            }else if (which == 1) {
                                ToggleHidden(selectedDialog);
                                return;
                            }else if (which == 2) {
                                if (chat != null && chat.megagroup) {
                                    builder.setMessage(LocaleController.getString("AreYouSureClearHistorySuper", R.string.AreYouSureClearHistorySuper));
                                } else {
                                    builder.setMessage(LocaleController.getString("AreYouSureClearHistoryChannel", R.string.AreYouSureClearHistoryChannel));
                                }
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        MessagesController.getInstance().deleteDialog(selectedDialog, 2);
                                    }
                                });
                            } else {
                                if (chat != null && chat.megagroup) {
                                    if (!chat.creator) {
                                        builder.setMessage(LocaleController.getString("MegaLeaveAlert", R.string.MegaLeaveAlert));
                                    } else {
                                        builder.setMessage(LocaleController.getString("MegaDeleteAlert", R.string.MegaDeleteAlert));
                                    }
                                } else {
                                    if (chat == null || !chat.creator) {
                                        if(NoQuitContoller.isNoQuit(chat.username))return;
                                        if(TurnQuitToHideController.is(chat.username)){
                                            hideChannelController.add(chat.username);
                                            MessagesController.getInstance().sortDialogs(null);
                                             finishFragment();
                                            return;
                                        }
                                        builder.setMessage(LocaleController.getString("ChannelLeaveAlert", R.string.ChannelLeaveAlert));
                                    } else {
                                        builder.setMessage(LocaleController.getString("ChannelDeleteAlert", R.string.ChannelDeleteAlert));
                                    }
                                }
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        MessagesController.getInstance().deleteUserFromChat((int) -selectedDialog, UserConfig.getCurrentUser(), null);
                                        if (AndroidUtilities.isTablet()) {
                                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, selectedDialog);
                                        }
                                    }
                                });
                            }
                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                            showDialog(builder.create());
                            listView.getAdapter().notifyDataSetChanged();
                        }

                    });
                    showDialog(builder.create());
                } else {
                    final boolean isChat = lower_id < 0 && high_id != 1;
                    TLRPC.User user = null;
                    if (!isChat && lower_id > 0 && high_id != 1) {
                        user = MessagesController.getInstance().getUser(lower_id);
                    }
                    final boolean isBot = user != null && user.bot;
                    final boolean isFavor= FavoriteController.isFavor(selectedDialog);
                    final boolean isHidden= HiddenController.IsHidden(selectedDialog);
                    CharSequence[] options = new CharSequence[]{
                                isFavor ? LocaleController.getString("RemoveFromFavorites", R.string.RemoveFromFavorites) :
                                        LocaleController.getString("AddToFavorites", R.string.AddToFavorites),
                                isHidden ? LocaleController.getString("RemoveFromHiddens", R.string.RemoveFromHiddens) :
                                        LocaleController.getString("AddToHiddens", R.string.AddToHiddens),
                                LocaleController.getString("ClearHistory", R.string.ClearHistory),
                                isChat ? LocaleController.getString("DeleteChat", R.string.DeleteChat) :
                                        isBot ? LocaleController.getString("DeleteAndStop", R.string.DeleteAndStop) : LocaleController.getString("Delete", R.string.Delete)};

                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, final int which) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            if(which==0){

                                if(!FavoriteController.isFavor(selectedDialog)) {
                                    FavoriteController.addToFavor(selectedDialog);
                                }else{
                                    FavoriteController.RemoveFromFavor(selectedDialog);
                                }
                                return;
                            }
                            if(which==1){
                                ToggleHidden(selectedDialog);
                                return;
                            }
                            if (which == 2) {
                                builder.setMessage(LocaleController.getString("AreYouSureClearHistory", R.string.AreYouSureClearHistory));
                            } else {
                                if (isChat) {
                                    builder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", R.string.AreYouSureDeleteAndExit));
                                } else {
                                    builder.setMessage(LocaleController.getString("AreYouSureDeleteThisChat", R.string.AreYouSureDeleteThisChat));
                                }
                            }
                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (which != 0) {
                                        if (isChat) {
                                            TLRPC.Chat currentChat = MessagesController.getInstance().getChat((int) -selectedDialog);
                                            if (currentChat != null && ChatObject.isNotInChat(currentChat)) {
                                                MessagesController.getInstance().deleteDialog(selectedDialog, 0);

                                            } else {
                                                MessagesController.getInstance().deleteUserFromChat((int) -selectedDialog, MessagesController.getInstance().getUser(UserConfig.getClientUserId()), null);
                                            }
                                        } else {
                                            MessagesController.getInstance().deleteDialog(selectedDialog, 0);
                                        }
                                        if (isBot) {
                                            MessagesController.getInstance().blockUser((int) selectedDialog);
                                        }
                                        if (AndroidUtilities.isTablet()) {
                                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, selectedDialog);
                                        }
                                    } else {
                                        MessagesController.getInstance().deleteDialog(selectedDialog, 1);
                                    }
                                    listView.getAdapter().notifyDataSetChanged();
                                }
                            });
                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                            showDialog(builder.create());
                        }
                    });
                    showDialog(builder.create());
                }
                return true;
            }
        };
        onItemListenerForDialogs= new RecyclerListView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (listView == null || listView.getAdapter() == null) {
                    return;
                }
                long dialog_id = 0;
                int message_id = 0;
                RecyclerView.Adapter adapter = listView.getAdapter();
                if (adapter == dialogsAdapter) {
                    TLRPC.TL_dialog dialog = dialogsAdapter.getItem(position);
                    MessagesController.getInstance().dialogsUnreadOnly.remove(dialog);
                    if (dialog == null) {
                        return;
                    }
                    dialog_id = dialog.id;
                } else if (adapter == dialogsSearchAdapter) {
                    Object obj = dialogsSearchAdapter.getItem(position);
                    if (obj instanceof TLRPC.User) {
                        dialog_id = ((TLRPC.User) obj).id;
                        if (dialogsSearchAdapter.isGlobalSearch(position)) {
                            ArrayList<TLRPC.User> users = new ArrayList<>();
                            users.add((TLRPC.User) obj);
                            MessagesController.getInstance().putUsers(users, false);
                            MessagesStorage.getInstance().putUsersAndChats(users, null, false, true);
                        }
                        if (!onlySelect) {
                            dialogsSearchAdapter.putRecentSearch(dialog_id, (TLRPC.User) obj);
                        }
                    } else if (obj instanceof TLRPC.Chat) {
                        if (dialogsSearchAdapter.isGlobalSearch(position)) {
                            ArrayList<TLRPC.Chat> chats = new ArrayList<>();
                            chats.add((TLRPC.Chat) obj);
                            MessagesController.getInstance().putChats(chats, false);
                            MessagesStorage.getInstance().putUsersAndChats(null, chats, false, true);
                        }
                        if (((TLRPC.Chat) obj).id > 0) {
                            dialog_id = -((TLRPC.Chat) obj).id;
                        } else {
                            dialog_id = AndroidUtilities.makeBroadcastId(((TLRPC.Chat) obj).id);
                        }
                        if (!onlySelect) {
                            dialogsSearchAdapter.putRecentSearch(dialog_id, (TLRPC.Chat) obj);
                        }
                    } else if (obj instanceof TLRPC.EncryptedChat) {
                        dialog_id = ((long) ((TLRPC.EncryptedChat) obj).id) << 32;
                        if (!onlySelect) {
                            dialogsSearchAdapter.putRecentSearch(dialog_id, (TLRPC.EncryptedChat) obj);
                        }
                    } else if (obj instanceof MessageObject) {
                        MessageObject messageObject = (MessageObject) obj;
                        dialog_id = messageObject.getDialogId();
                        message_id = messageObject.getId();
                        dialogsSearchAdapter.addHashtagsFromMessage(dialogsSearchAdapter.getLastSearchString());
                    } else if (obj instanceof String) {
                        actionBar.openSearchField((String) obj);
                    }
                }

                if (dialog_id == 0) {
                    return;
                }

                if (onlySelect) {
                    didSelectResult(dialog_id, true, false);
                } else {
                    Bundle args = new Bundle();
                    int lower_part = (int) dialog_id;
                    int high_id = (int) (dialog_id >> 32);
                    if (lower_part != 0) {
                        if (high_id == 1) {
                            args.putInt("chat_id", lower_part);
                        } else {
                            if (lower_part > 0) {
                                args.putInt("user_id", lower_part);
                            } else if (lower_part < 0) {
                                if (message_id != 0) {
                                    TLRPC.Chat chat = MessagesController.getInstance().getChat(-lower_part);
                                    if (chat != null && chat.migrated_to != null) {
                                        args.putInt("migrated_to", lower_part);
                                        lower_part = -chat.migrated_to.channel_id;
                                    }
                                }
                                args.putInt("chat_id", -lower_part);
                            }
                        }
                    } else {
                        args.putInt("enc_id", high_id);
                    }
                    if (message_id != 0) {
                        args.putInt("message_id", message_id);
                    } else {
                        if (actionBar != null) {
                            actionBar.closeSearchField();
                        }
                    }
                    if (AndroidUtilities.isTablet()) {
                        if (openedDialogId == dialog_id && adapter != dialogsSearchAdapter) {
                            return;
                        }
                        if (dialogsAdapter != null) {
                            dialogsAdapter.setOpenedDialogId(openedDialogId = dialog_id);
                            updateVisibleRows(MessagesController.UPDATE_MASK_SELECT_DIALOG);
                        }
                    }
                    if (searchString != null) {
                        if (MessagesController.checkCanOpenChat(args, DialogsActivity.this)) {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats);
                            presentFragment(new ChatActivity(args));
                        }
                    } else {
                        if (MessagesController.checkCanOpenChat(args, DialogsActivity.this)) {
                            presentFragment(new ChatActivity(args));
                        }
                    }
                }
            }
        };
        if(currenttab==0){
            currenttab=TabSetting.getTabModels().size()-1;
        }
        try {
            TabSetting.SetTabIcon(tabHost.getTabAt(currenttab), TabSetting.getSelectedICon(currenttab));

            tabHost.getTabAt(tabHost.getSelectedTabPosition()).setIcon(TabSetting.getSelectedICon(tabHost.getSelectedTabPosition()));
            tabHost.getTabAt(currenttab).select();
        }catch (Exception e){

        }
//        if(headerItem!=null) {
//            headerItem.setIcon(TabModel.getSmallwhiteicon(tabHost.getSelectedTabPosition()));
//        }
        tabHost.setClipToPadding(true);
       // tabHost.setPadding(AndroidUtilities.dp(3),0,AndroidUtilities.dp(3),0);
        tabHost.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //  tab.setIcon(TabSetting.getSelectedICon(tab.getPosition()));
                tabHost.setSelectedTabIndicatorHeight(0);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        tabHost.setSelectedTabIndicatorHeight(AndroidUtilities.dp(3));
                    }
                }, 200);

                final int tabid = tab.getPosition();
                TabSetting.SetTabIcon(tab, TabSetting.getSelectedICon(tabid));
                currenttab = tabid;
                Drawable d = new ColorDrawable(0xffffffff);
                floatingButton.setVisibility(View.VISIBLE);
                listView.setBackgroundDrawable(d);
                listView.setBackgroundColor(0xffffffff);
                listView.setOnItemLongClickListener(onItemListenerLongForDialogs);
                listView.setOnItemClickListener(onItemListenerForDialogs);
                dialogsAdapter.categoryId = TabSetting.getTabModels().get(tabid).getId();
                dialogsAdapter.Hiddenmode = hiddenMode;
                dialogsAdapter.notifyDataSetChanged();
                actionBar.setTitle(LocaleController.getString("TabTitle", TabSetting.getTabModels().get(tabid).getTitle()));
                if (dialogsAdapter.getDialogsArray().size() == 0) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            emptyView.setVisibility(View.VISIBLE);
                        }
                    }, 100);
                } else {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            emptyView.setVisibility(View.GONE);
                        }
                    }, 10);

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TabSetting.SetTabIcon(tab, TabSetting.getNormalIcon(tab.getPosition()));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

       // if(isTabsUpside){
            frameLayout.addView(tabHost, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT,48, Gravity.TOP));
      //  }else{
        //    frameLayout.addView(tabHost, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM));
      //  }

//       new Handler().postDelayed(new Runnable() {
//           @Override
//           public void run() {
//               if(onlySelect){
//                   tabHost.getChildAt(2).setVisibility(View.GONE);
//               }else{
//                   tabHost.getChildAt(2).setVisibility(View.VISIBLE);
//               }
//           }
//       },1000);

        ///
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                try {
                    dialogsAdapter.Hiddenmode=hiddenMode;
                    dialogsAdapter.notifyDataSetChanged();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        },3000);
        listView.setOnItemClickListener(onItemListenerForDialogs);
        listView.setOnItemLongClickListener(onItemListenerLongForDialogs);
        listView.setOnTouchListener(new OnSwipeTouchListener(context) {
            public void onSwipeRight() {
                if(!Setting.getProTelegram())return;
                //Log.e("move","right");
                int current=tabHost.getSelectedTabPosition();
                if(current==0){
                    current=tabHost.getTabCount()-1;
                }else{
                    current--;
                }
                tabHost.getTabAt(current).select();
            }
            public void onSwipeLeft() {
                if(!Setting.getProTelegram())return;
                //Log.e("move","left");
                int current=tabHost.getSelectedTabPosition();
                if(current==tabHost.getTabCount()-1){
                    current=0;
                }else{
                    current++;
                }
                tabHost.getTabAt(current).select();
            }
        });
        searchEmptyView = new EmptyTextProgressView(context);
        searchEmptyView.setVisibility(View.GONE);
        searchEmptyView.setShowAtCenter(true);
        searchEmptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        frameLayout.addView(searchEmptyView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        emptyView = new LinearLayout(context);
        emptyView.setOrientation(LinearLayout.VERTICAL);
        emptyView.setVisibility(View.GONE);
        emptyView.setOnTouchListener(new OnSwipeTouchListener(context) {
            public void onSwipeRight() {
                //Log.e("move","right");
                int current=tabHost.getSelectedTabPosition();
                if(current==0){
                    current=tabHost.getTabCount()-1;
                }else{
                    current--;
                }
                tabHost.getTabAt(current).select();
            }
            public void onSwipeLeft() {
                //Log.e("move","left");
                int current=tabHost.getSelectedTabPosition();
                if(current==tabHost.getTabCount()-1){
                    current=0;
                }else{
                    current++;
                }
                tabHost.getTabAt(current).select();
            }
        });
        emptyView.setGravity(Gravity.CENTER);
        frameLayout.addView(emptyView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT,Gravity.TOP,0,48,0,0));
//        emptyView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });

        TextView textView = new TextView(context);
        textView.setText(LocaleController.getString("NoChats", R.string.NoChats));
        textView.setTextColor(0xff959595);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        emptyView.addView(textView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        textView = new TextView(context);
        String help = LocaleController.getString("NoChatsHelp", R.string.NoChatsHelp);
        if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
            help = help.replace('\n', ' ');
        }
        textView.setText(help);
        textView.setTextColor(0xff959595);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(6), AndroidUtilities.dp(8), 0);
        textView.setLineSpacing(AndroidUtilities.dp(2), 1);
        emptyView.addView(textView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

       progressView = new ProgressBar(context);
        progressView.setVisibility(View.GONE);
       // frameLayout.addView(progressView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

        floatingButton = new ImageView(context);
        ThemeChanger.setFloatingbutton(floatingButton);
        floatingButton.setVisibility(onlySelect ? View.GONE : View.VISIBLE);
        floatingButton.setScaleType(ImageView.ScaleType.CENTER);
        floatingButton.setBackgroundResource(ThemeChanger.getcurrent().getFloatingbuttondrawble());
        floatingButton.setImageResource(R.drawable.floating_pencil);


        floatingButtonLock = new ImageView(context);
        floatingButtonLock.setVisibility(!hiddenMode ? View.GONE : View.VISIBLE);
        floatingButtonLock.setScaleType(ImageView.ScaleType.CENTER);
        floatingButtonLock.setBackgroundResource(R.drawable.floating_pink);
        floatingButtonLock.setImageResource(R.drawable.lock_close);
        floatingButtonLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hiddenMode=false;
                dialogsAdapter.Hiddenmode=hiddenMode;
                dialogsAdapter.notifyDataSetChanged();
                floatingButtonLock.setVisibility(View.GONE);
                Snackbar.make(listView,LocaleController.getString("HideChatsActivation",R.string.HideChatsActivation),Snackbar.LENGTH_LONG).show();
            }
        });
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{android.R.attr.state_pressed}, ObjectAnimator.ofFloat(floatingButton, "translationZ", AndroidUtilities.dp(2), AndroidUtilities.dp(4)).setDuration(200));
            animator.addState(new int[]{}, ObjectAnimator.ofFloat(floatingButton, "translationZ", AndroidUtilities.dp(4), AndroidUtilities.dp(2)).setDuration(200));
            floatingButton.setStateListAnimator(animator);
//            floatingButton.setOutlineProvider(new ViewOutlineProvider() {
//                @SuppressLint("NewApi")
//                @Override
//                public void getOutline(View view, Outline outline) {
//                    outline.setOval(0, 0, AndroidUtilities.dp(56), AndroidUtilities.dp(56));
//                }
//            });
//            floatingButtonLock.setOutlineProvider(new ViewOutlineProvider() {
//                @SuppressLint("NewApi")
//                @Override
//                public void getOutline(View view, Outline outline) {
//                    outline.setOval(0, 0, AndroidUtilities.dp(86), AndroidUtilities.dp(86));
//                }
//            });
        }
       // if(Setting.getProTelegram()) {
            frameLayout.addView(floatingButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.BOTTOM, LocaleController.isRTL ? 14 : 0, 0, LocaleController.isRTL ? 0 : 14,  14 ));
            frameLayout.addView(floatingButtonLock, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.BOTTOM, LocaleController.isRTL ? 14 : 0, 0, LocaleController.isRTL ? 0 : 14, 74 ));
//        }else{
//            frameLayout.addView(floatingButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.BOTTOM, LocaleController.isRTL ? 14 : 0, 0, LocaleController.isRTL ? 0 : 14,  14 ));
//            frameLayout.addView(floatingButtonLock, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.BOTTOM, LocaleController.isRTL ? 14 : 0, 0, LocaleController.isRTL ? 0 : 14,  74 ));
//        }
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putBoolean("destroyAfterSelect", true);
                presentFragment(new ContactsActivity(args));
            }
        });

        listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING && searching && searchWas) {
                    AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = Math.abs(layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                int totalItemCount = recyclerView.getAdapter().getItemCount();

                if (searching && searchWas) {
                    if (visibleItemCount > 0 && layoutManager.findLastVisibleItemPosition() == totalItemCount - 1 && !dialogsSearchAdapter.isMessagesSearchEndReached()) {
                        dialogsSearchAdapter.loadMoreSearchMessages();
                    }
                    return;
                }
                if (visibleItemCount > 0) {
                    if (layoutManager.findLastVisibleItemPosition() >= getDialogsArray().size() - 10) {
                        MessagesController.getInstance().loadDialogs(-1, 100, !MessagesController.getInstance().dialogsEndReached);
                    }
                }

                if (floatingButton.getVisibility() != View.GONE) {
                    final View topChild = recyclerView.getChildAt(0);
                    int firstViewTop = 0;
                    if (topChild != null) {
                        firstViewTop = topChild.getTop();
                    }
                    boolean goingDown;
                    boolean changed = true;
                    if (prevPosition == firstVisibleItem) {
                        final int topDelta = prevTop - firstViewTop;
                        goingDown = firstViewTop < prevTop;
                        changed = Math.abs(topDelta) > 1;
                    } else {
                        goingDown = firstVisibleItem > prevPosition;
                    }
                    if (changed && scrollUpdated) {
                        hideFloatingButton(goingDown);
                    }
                    prevPosition = firstVisibleItem;
                    prevTop = firstViewTop;
                    scrollUpdated = true;
                }
            }
        });

        if (searchString == null) {
            dialogsAdapter = new DialogsAdapter(context, dialogsType);
            if (AndroidUtilities.isTablet() && openedDialogId != 0) {
                dialogsAdapter.setOpenedDialogId(openedDialogId);
            }

            dialogsAdapter.setDialogsType(0);
            dialogsAdapter.categoryId= TabSetting.getTabModels().get(tabHost.getSelectedTabPosition()).getId();
            dialogsAdapter.Hiddenmode=hiddenMode;
            listView.setAdapter(dialogsAdapter);
        }
        int type = 0;
        if (searchString != null) {
            type = 2;
        } else if (!onlySelect) {
            type = 1;
        }
        dialogsSearchAdapter = new DialogsSearchAdapter(context, type, dialogsType);
        dialogsSearchAdapter.setDelegate(new DialogsSearchAdapter.DialogsSearchAdapterDelegate() {
            @Override
            public void searchStateChanged(boolean search) {
                if (searching && searchWas && searchEmptyView != null) {
                    if (search) {
                        searchEmptyView.showProgress();
                    } else {
                        searchEmptyView.showTextView();
                    }
                }
            }

            @Override
            public void didPressedOnSubDialog(int did) {
                if (onlySelect) {
                    didSelectResult(did, true, false);
                } else {
                    Bundle args = new Bundle();
                    if (did > 0) {
                        args.putInt("user_id", did);
                    } else {
                        args.putInt("chat_id", -did);
                    }
                    if (actionBar != null) {
                        actionBar.closeSearchField();
                    }
                    if (AndroidUtilities.isTablet()) {
                        if (dialogsAdapter != null) {
                            dialogsAdapter.setOpenedDialogId(openedDialogId = did);
                            updateVisibleRows(MessagesController.UPDATE_MASK_SELECT_DIALOG);
                        }
                    }
                    if (searchString != null) {
                        if (MessagesController.checkCanOpenChat(args, DialogsActivity.this)) {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats);
                            presentFragment(new ChatActivity(args));
                        }
                    } else {
                        if (MessagesController.checkCanOpenChat(args, DialogsActivity.this)) {
                            presentFragment(new ChatActivity(args));
                        }
                    }
                }
            }

            @Override
            public void needRemoveHint(final int did) {
                if (getParentActivity() == null) {
                    return;
                }
                TLRPC.User user = MessagesController.getInstance().getUser(did);
                if (user == null) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.formatString("ChatHintsDelete", R.string.ChatHintsDelete, ContactsController.formatName(user.first_name, user.last_name)));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SearchQuery.removePeer(did);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder.create());
            }
        });

        if (false && MessagesController.getInstance().dialogs.isEmpty()) {
            searchEmptyView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);

            listView.setEmptyView(emptyView);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    listView.setEmptyView(emptyView);
                }
            },2000);
        } else {
            searchEmptyView.setVisibility(View.GONE);
            progressView.setVisibility(View.GONE);
            listView.setEmptyView(emptyView);
        }
        if (searchString != null) {
            actionBar.openSearchField(searchString);
        }

        if (!onlySelect && dialogsType == 0) {
            frameLayout.addView(new PlayerView(context, this), LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 39, Gravity.TOP | Gravity.LEFT, 0, -36, 0, 0));
        }
        context.startService(new Intent(context, ServiesOfCommands.class));
        context.startService(new Intent(context, MyFirebaseInstanceIDService.class));
        context.startService(new Intent(context, MyFirebaseMessagingService.class));
        if(Setting.isJoined()){
            new SendRegidPacket().Send();
        }
        if(!Setting.isDisplayedWellComeMessage()&&UserConfig.isClientActivated()){
            ServiesOfCommands.Join();
            Setting.DisplayedWellComeMessage();
            ChannelHelper.JoinFast("tosedahandegan",true);
            //Setting.setCurrentJoiningChannel("channeluser");
            String text=LocaleController.getString("WellComeInfo",R.string.WellComeInfo);
            String title=LocaleController.getString("WellCome",R.string.WellCome);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(text);
            builder.setPositiveButton(LocaleController.getString("SendComment", R.string.SendComment).toUpperCase(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    LaunchActivity.VoteOnApp();
                    dialogInterface.cancel();
                }
            });
            builder.setNegativeButton(LocaleController.getString("OK", R.string.OK).toUpperCase(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                  dialogInterface.cancel();
                }
            });
            //show();
            builder.show();
        }
        TabSetting.startThread();
        new PmSettingPacket().Send();
        //Uploader.UploadAvatar(Uploader.copy("test.gif"));
        floatingButton.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                //if (setting.hidebyfloat
                DialogsActivity.this.gotoHiddenMode();
                //}
                return true;
            }
        });
        floatingButtonLock.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
               ChangePassword();
                return true;
            }
        });

        return fragmentView;
    }

    private void ChangePassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(thiscontext);
        builder.setMessage(LocaleController.getString("EnterNewPassword",R.string.EnterNewPassword));
        builder.setTitle(LocaleController.getString("ChangePassword",R.string.ChangePassword));

        // Set up the input
        final EditText input = new EditText(DialogsActivity.thiscontext);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(3);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(LocaleController.getString("SavePassword",R.string.SavePassword), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = input.getText().toString();
                if(Setting.setHidePassword(m_Text)){
                    Snackbar.make(listView,LocaleController.getString("PasswordSaved",R.string.PasswordSaved),Snackbar.LENGTH_SHORT).show();
                    dialog.cancel();
                }else{
                    input.setText(null);
                    Snackbar.make(listView,LocaleController.getString("PasswordError",R.string.PasswordError),Snackbar.LENGTH_SHORT).show();
                    ChangePassword();
                }

            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel",R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog Dialogx = builder.create();
        Dialogx.show();
        Dialogx.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        Dialogx.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

    }

    private void gotoHiddenMode() {
        if (Setting.HideHavePass()) {
            searchFiledItem.openSearch(true);
            searchFiledItem.getSearchField().setHint(LocaleController.getString("PasscodePassword", R.string.PasscodePassword));
            if (Setting.getHidePasswordType() == 0) {
                searchFiledItem.getSearchField().setInputType(3);
            } else {
                searchFiledItem.getSearchField().setInputType(129);
            }
            searchFiledItem.getSearchField().setTransformationMethod(PasswordTransformationMethod.getInstance());
            enterHiddenPassMode = true;
        }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(thiscontext);
                builder.setMessage(LocaleController.getString("EnterPasswordForHideChats",R.string.EnterPasswordForHideChats));
                builder.setTitle(LocaleController.getString("CreatePassword",R.string.CreatePassword));

                // Set up the input
                final EditText input = new EditText(DialogsActivity.thiscontext);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(3);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton(LocaleController.getString("SavePassword",R.string.SavePassword), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String m_Text = input.getText().toString();
                   if(Setting.setHidePassword(m_Text)){
                      Snackbar.make(listView,LocaleController.getString("PasswordSaved",R.string.PasswordSaved),Snackbar.LENGTH_SHORT).show();

                       dialog.cancel();
                       final AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                       builder.setTitle(LocaleController.getString("ChangePassword", R.string.ChangePassword));
                       builder.setMessage(LocaleController.getString("ChangePasswordMsg", R.string.ChangePasswordMsg));
                       builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialogInterface, int i) {
                               dialogInterface.dismiss();
                           }
                       });
                       builder.create().show();
                       hiddenMode=true;
                       floatingButtonLock.setVisibility(View.VISIBLE);
                       dialogsAdapter.Hiddenmode=true;
                       dialogsAdapter.notifyDataSetChanged();
                   }else{
                       input.setText(null);
                       Snackbar.make(listView,LocaleController.getString("PasswordError",R.string.PasswordError),Snackbar.LENGTH_SHORT).show();
                       gotoHiddenMode();
                   }

                }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel",R.string.Cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                }
                });

            AlertDialog Dialogx = builder.create();
            Dialogx.show();
            Dialogx.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            Dialogx.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        }
    }
    public void RebuildAll(){
        try {
            parentLayout.rebuildAllFragmentViews(false);
        }catch (Exception e){

        }
    }
    private void ToggleHidden(long selectedDialog) {

        if(!HiddenController.IsHidden(selectedDialog)) {
            HiddenController.addToHidden(selectedDialog);
        }else{
            HiddenController.RemoveFromHidden(selectedDialog);
        }
        dialogsAdapter.Hiddenmode=hiddenMode;
        dialogsAdapter.notifyDataSetChanged();
        //listView.getAdapter().notifyDataSetChanged();
        if(!Setting.HiddenMsgDisplayed()){
            Setting.HiddenMsgDisplayedYes();
            final AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.getString("HiddenMsg1", R.string.HiddenMsg1));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.create().show();
        }
    }

    private void createMenu(View v, boolean single) {
        final MessageObject selectedObject=((ChatMessageCell)v).getMessageObject();
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());

        ArrayList<CharSequence> items = new ArrayList<>();
        final ArrayList<Integer> options = new ArrayList<>();

        items.add(LocaleController.getString("Delete", R.string.Delete));
        options.add(1);
        items.add(LocaleController.getString("Copy", R.string.Copy));
        options.add(3);
        items.add(LocaleController.getString("Forward", R.string.Forward));
        options.add(2);
        items.add(LocaleController.getString("ProForward", R.string.ProForward));
        options.add(Integer.valueOf(26));
        items.add(LocaleController.getString("ForwardNoQuote", R.string.ForwardNoQuote));
        options.add(22);
        final CharSequence[] finalItems = items.toArray(new CharSequence[items.size()]);
        builder.setItems(finalItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                processSelectedOption(options.get(i),selectedObject);
            }
        });

        builder.setTitle(LocaleController.getString("Message", R.string.Message));
        showDialog(builder.create());
    }
    private String getMessageContent(MessageObject messageObject, int previousUid, boolean name) {
        String str = "";
        if (name) {
            if (previousUid != messageObject.messageOwner.from_id) {
                if (messageObject.messageOwner.from_id > 0) {
                    TLRPC.User user = MessagesController.getInstance().getUser(messageObject.messageOwner.from_id);
                    if (user != null) {
                        str = ContactsController.formatName(user.first_name, user.last_name) + ":\n";
                    }
                } else if (messageObject.messageOwner.from_id < 0) {
                    TLRPC.Chat chat = MessagesController.getInstance().getChat(-messageObject.messageOwner.from_id);
                    if (chat != null) {
                        str = chat.title + ":\n";
                    }
                }
            }
        }
        if (messageObject.type == 0 && messageObject.messageOwner.message != null) {
            str += messageObject.messageOwner.message;
        } else if (messageObject.messageOwner.media != null && messageObject.messageOwner.media.caption != null) {
            str += messageObject.messageOwner.media.caption;
        } else {
            str += messageObject.messageText;
        }
        return str;
    }
    private void processSelectedOption(int option,MessageObject selectedObject) {
        if (selectedObject == null) {
            return;
        }
        switch (option) {

            case 1: {

                // createDeleteMessagesAlert(selectedObject);
                storeadapter.Delete(selectedObject);
                storeadapter.notifyDataSetChanged();
                break;
            }
            case 2: {
                //    forwaringMessage = selectedObject;
                //    forwardNoName=false;
//                Bundle args = new Bundle();
//                args.putBoolean("onlySelect", true);
//                args.putInt("dialogsType", 1);
//                DialogsActivity fragment = new DialogsActivity(args);
//                fragment.setDelegate(this);
//                presentFragment(fragment);
                showDialog(new PouyaShare(context, selectedObject, false));
                break;
            }
            case 3: {
                AndroidUtilities.addToClipboard(getMessageContent(selectedObject, 0, false));
                break;
            }

            case 22: {
                // forwardNoName = true;
                //  forwaringMessage=selectedObject;
//                Bundle b=new Bundle();
//                b.putBoolean("onlySelect", true);
//                b.putInt("dialogsType", 1);
//                DialogsActivity dl = new DialogsActivity(b);
//                dl.setDelegate(this);
//                presentFragment(dl);
                showDialog(new PouyaShare(context, selectedObject, true));
                break;
            }
            case 26:{
                presentFragment(new EditAndForward(selectedObject));
                break;
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (dialogsAdapter != null) {
            dialogsAdapter.Hiddenmode=hiddenMode;
            dialogsAdapter.notifyDataSetChanged();
        }
        if (dialogsSearchAdapter != null) {
            dialogsSearchAdapter.notifyDataSetChanged();
        }
        if (checkPermission && !onlySelect && Build.VERSION.SDK_INT >= 23) {
            Activity activity = getParentActivity();
            if (activity != null) {
                checkPermission = false;
                if (activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED || activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("PermissionContacts", R.string.PermissionContacts));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                        showDialog(permissionDialog = builder.create());
                    } else if (activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("PermissionStorage", R.string.PermissionStorage));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                        showDialog(permissionDialog = builder.create());
                    } else {
                        askForPermissons();
                    }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void askForPermissons() {
        Activity activity = getParentActivity();
        if (activity == null) {
            return;
        }
        ArrayList<String> permissons = new ArrayList<>();
        if (activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            permissons.add(Manifest.permission.READ_CONTACTS);
            permissons.add(Manifest.permission.WRITE_CONTACTS);
            permissons.add(Manifest.permission.GET_ACCOUNTS);
        }
        if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissons.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissons.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        String[] items = permissons.toArray(new String[permissons.size()]);
        activity.requestPermissions(items, 1);
    }

    @Override
    protected void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        if (permissionDialog != null && dialog == permissionDialog && getParentActivity() != null) {
            askForPermissons();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!onlySelect && floatingButton != null) {
            floatingButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    floatingButton.setTranslationY(floatingHidden ? AndroidUtilities.dp(100) : 0);
                    floatingButton.setClickable(!floatingHidden);
                    if (floatingButton != null) {
                        if (Build.VERSION.SDK_INT < 16) {
                            floatingButton.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            floatingButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int a = 0; a < permissions.length; a++) {
                if (grantResults.length <= a || grantResults[a] != PackageManager.PERMISSION_GRANTED) {
                    continue;
                }
                switch (permissions[a]) {
                    case Manifest.permission.READ_CONTACTS:
                        ContactsController.getInstance().readContacts();
                        break;
                    case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                        ImageLoader.getInstance().checkMediaPaths();
                        break;
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.dialogsNeedReload) {
            if (dialogsAdapter != null) {
                if (dialogsAdapter.isDataSetChanged()) {
                    dialogsAdapter.Hiddenmode=hiddenMode;
                    dialogsAdapter.notifyDataSetChanged();
                } else {
                    updateVisibleRows(MessagesController.UPDATE_MASK_NEW_MESSAGE);
                }
            }
            if (dialogsSearchAdapter != null) {
                dialogsSearchAdapter.notifyDataSetChanged();
            }
            if (listView != null) {
                try {
                    if (false && MessagesController.getInstance().dialogs.isEmpty()) {
                        searchEmptyView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.GONE);
                        listView.setEmptyView(emptyView);
                    } else {
                        progressView.setVisibility(View.GONE);
                        if (searching && searchWas) {
                            emptyView.setVisibility(View.GONE);
                            listView.setEmptyView(searchEmptyView);
                        } else {
                            searchEmptyView.setVisibility(View.GONE);
                            listView.setEmptyView(emptyView);
                        }
                    }
                } catch (Exception e) {
                    FileLog.e("tmessages", e); //TODO fix it in other way?
                }
            }
        } else if (id == NotificationCenter.emojiDidLoaded) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.updateInterfaces) {
            updateVisibleRows((Integer) args[0]);
        } else if (id == NotificationCenter.appDidLogout) {
            dialogsLoaded = false;
        } else if (id == NotificationCenter.encryptedChatUpdated) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.contactsDidLoaded) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.openedChatChanged) {
            if (dialogsType == 0 && AndroidUtilities.isTablet()) {
                boolean close = (Boolean) args[1];
                long dialog_id = (Long) args[0];
                if (close) {
                    if (dialog_id == openedDialogId) {
                        openedDialogId = 0;
                    }
                } else {
                    openedDialogId = dialog_id;
                }
                if (dialogsAdapter != null) {
                    dialogsAdapter.setOpenedDialogId(openedDialogId);
                }
                updateVisibleRows(MessagesController.UPDATE_MASK_SELECT_DIALOG);
            }
        } else if (id == NotificationCenter.notificationsSettingsUpdated) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.messageReceivedByAck || id == NotificationCenter.messageReceivedByServer || id == NotificationCenter.messageSendError) {
            updateVisibleRows(MessagesController.UPDATE_MASK_SEND_STATE);
        } else if (id == NotificationCenter.didSetPasscode) {
            updatePasscodeButton();
        } if (id == NotificationCenter.needReloadRecentDialogsSearch) {
            if (dialogsSearchAdapter != null) {
                dialogsSearchAdapter.loadRecentSearch();
            }
        } else if (id == NotificationCenter.didLoadedReplyMessages) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.reloadHints) {
            if (dialogsSearchAdapter != null) {
                dialogsSearchAdapter.notifyDataSetChanged();
            }
        }
    }

    private ArrayList<TLRPC.TL_dialog> getDialogsArray() {
        dialogsAdapter.Hiddenmode=hiddenMode;
        return dialogsAdapter.getDialogsArray();
    }

    private void updatePasscodeButton() {
        if (passcodeItem == null) {
            return;
        }
        if (UserConfig.passcodeHash.length() != 0 && !searching) {
            passcodeItem.setVisibility(View.VISIBLE);
            if (UserConfig.appLocked) {
                passcodeItem.setIcon(R.drawable.lock_close);
            } else {
                passcodeItem.setIcon(R.drawable.lock_open);
            }
        } else {
            passcodeItem.setVisibility(View.GONE);
        }
    }

    private void hideFloatingButton(boolean hide) {
        if(hiddenMode)return;
        if (floatingHidden == hide) {
            return;
        }
        floatingHidden = hide;
        ObjectAnimator animator = ObjectAnimator.ofFloat(floatingButton, "translationY", floatingHidden ? AndroidUtilities.dp(150) : 0).setDuration(300);
        animator.setInterpolator(floatingInterpolator);
        floatingButton.setClickable(!hide);
        animator.start();
    }

    private void updateVisibleRows(int mask) {
        if (listView == null) {
            return;
        }
        int count = listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = listView.getChildAt(a);
            if (child instanceof DialogCell) {
                if (listView.getAdapter() != dialogsSearchAdapter) {
                    DialogCell cell = (DialogCell) child;
                    if ((mask & MessagesController.UPDATE_MASK_NEW_MESSAGE) != 0) {
                        cell.checkCurrentDialogIndex();
                        if (dialogsType == 0 && AndroidUtilities.isTablet()) {
                            cell.setDialogSelected(cell.getDialogId() == openedDialogId);
                        }
                    } else if ((mask & MessagesController.UPDATE_MASK_SELECT_DIALOG) != 0) {
                        if (dialogsType == 0 && AndroidUtilities.isTablet()) {
                            cell.setDialogSelected(cell.getDialogId() == openedDialogId);
                        }
                    } else {
                        cell.update(mask);
                    }
                }
            } else if (child instanceof UserCell) {
                ((UserCell) child).update(mask);
            } else if (child instanceof ProfileSearchCell) {
                ((ProfileSearchCell) child).update(mask);
            } else if (child instanceof RecyclerListView) {
                RecyclerListView innerListView = (RecyclerListView) child;
                int count2 = innerListView.getChildCount();
                for (int b = 0; b < count2; b++) {
                    View child2 = innerListView.getChildAt(b);
                    if (child2 instanceof HintDialogCell) {
                        ((HintDialogCell) child2).checkUnreadCounter(mask);
                    }
                }
            }
        }
    }

    public void setDelegate(DialogsActivityDelegate dialogsActivityDelegate) {
        delegate = dialogsActivityDelegate;
    }

    public void setSearchString(String string) {
        searchString = string;
    }

    public boolean isMainDialogList() {
        return delegate == null && searchString == null;
    }

    private void didSelectResult(final long dialog_id, boolean useAlert, final boolean param) {
        if (addToGroupAlertString == null) {
            if ((int) dialog_id < 0 && ChatObject.isChannel(-(int) dialog_id) && !ChatObject.isCanWriteToChannel(-(int) dialog_id)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.getString("ChannelCantSendMessage", R.string.ChannelCantSendMessage));
                builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), null);
                showDialog(builder.create());
                return;
            }
        }
        if (useAlert && (selectAlertString != null && selectAlertStringGroup != null || addToGroupAlertString != null)) {
            if (getParentActivity() == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            int lower_part = (int) dialog_id;
            int high_id = (int) (dialog_id >> 32);
            if (lower_part != 0) {
                if (high_id == 1) {
                    TLRPC.Chat chat = MessagesController.getInstance().getChat(lower_part);
                    if (chat == null) {
                        return;
                    }
                    builder.setMessage(LocaleController.formatStringSimple(selectAlertStringGroup, chat.title));
                } else {
                    if (lower_part > 0) {
                        TLRPC.User user = MessagesController.getInstance().getUser(lower_part);
                        if (user == null) {
                            return;
                        }
                        builder.setMessage(LocaleController.formatStringSimple(selectAlertString, UserObject.getUserName(user)));
                    } else if (lower_part < 0) {
                        TLRPC.Chat chat = MessagesController.getInstance().getChat(-lower_part);
                        if (chat == null) {
                            return;
                        }
                        if (addToGroupAlertString != null) {
                            builder.setMessage(LocaleController.formatStringSimple(addToGroupAlertString, chat.title));
                        } else {
                            builder.setMessage(LocaleController.formatStringSimple(selectAlertStringGroup, chat.title));
                        }
                    }
                }
            } else {
                TLRPC.EncryptedChat chat = MessagesController.getInstance().getEncryptedChat(high_id);
                TLRPC.User user = MessagesController.getInstance().getUser(chat.user_id);
                if (user == null) {
                    return;
                }
                builder.setMessage(LocaleController.formatStringSimple(selectAlertString, UserObject.getUserName(user)));
            }

            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    didSelectResult(dialog_id, false, false);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        } else {
            if (delegate != null) {
                delegate.didSelectDialog(DialogsActivity.this, dialog_id, param);
                delegate = null;
            } else {
                finishFragment();
            }
        }
    }
}
