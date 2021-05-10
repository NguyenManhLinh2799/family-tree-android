package me.jagar.mindmappingandroidlibrary.Views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class Item extends LinearLayout {

    public float X;
    public int Y;
    public float Mod;

    public Item Parent = null;
    public ArrayList<Item> Children = new ArrayList<>(0);
    public Item Family = null;
    public Item Partner = null;
    public int type;

    // If type == FAMILY then it will have husband and wife
    public Item Husband = null;
    public Item Wife = null;

    Context context;
    TextView title;
    TextView content;
    boolean defaultStyle;
    HashMap<Connection, Integer> connections = new HashMap<>();
    HashMap<Item, Integer>  parents = new HashMap<>();

    // My customization
    public boolean isLeaf() {
        return this.Children.size() == 0;
    }

    public boolean isLeftMost() {
        if (this.Parent == null) {
            return true;
        }
        return this.Parent.Children.get(0) == this;
    }

    public boolean isRightMost() {
        if (this.Parent == null) {
            return true;
        }
        return this.Parent.Children.get(this.Parent.Children.size() - 1) == this;
    }

    public Item getPreviousSibling() {
        if (this.Parent == null || this.isLeftMost()) {
            return null;
        }
        return this.Parent.Children.get(this.Parent.Children.indexOf(this) - 1);
    }

    public Item getNextSibling() {
        if (this.Parent == null || this.isRightMost()) {
            return null;
        }
        return this.Parent.Children.get(this.Parent.Children.indexOf(this) + 1);
    }

    public Item getLeftMostSibling() {
        if (this.Parent == null) {
            return null;
        }
        if (this.isLeftMost()) {
            return this;
        }
        return this.Parent.Children.get(0);
    }

    public Item getLeftMostChild() {
        if (this.Children.size() == 0) {
            return null;
        }
        return this.Children.get(0);
    }

    public Item getRightMostChild() {
        if (this.Children.size() == 0) {
            return null;
        }
        return this.Children.get(this.Children.size() - 1);
    }
    // My customization

    public Item(Context context, String title, String content, int type){
        super(context);
        this.context = context;
        this.type = type;
        this.setTitle(title);
        this.setContent(content);
        this.addTextViews();

        if (title == null)
            this.title.setVisibility(GONE);
        if (content == null)
            this.content.setVisibility(GONE);
    }

    public Item(Context context) {
        super(context);
        this.context = context;
    }

    public Item(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Item(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTitle(String title){
        this.title = new TextView(context);
        this.getTitle().setText(title);
        this.getTitle().setTypeface(Typeface.DEFAULT_BOLD);
    }
    public void setContent(String content){
        this.content = new TextView(context);
        this.getContent().setText(content);
        this.getContent().setTypeface(Typeface.DEFAULT);
    }
    public void setBorder(int color, int size){
        GradientDrawable drawable = (GradientDrawable)this.getBackground();
        drawable.setStroke(size, color);
    }

    public TextView getTitle(){
        return this.title;
    }
    public TextView getContent(){
        return this.content;
    }

    private void addTextViews(){
        this.setOrientation(LinearLayout.VERTICAL);
        this.addView(title);
        this.addView(content);

        if (defaultStyle)
            setDefaultStyle();

    }

    //If the item default style is true
    private void setDefaultStyle(){
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(Color.GRAY);
        shape.setCornerRadius(100);
        this.setBackground(shape);
        this.setBorder(Color.BLACK, 5);
        this.setGravity(Gravity.CENTER);
        this.title.setGravity(Gravity.CENTER);
        this.content.setGravity(Gravity.CENTER);

        this.setPadding(50, 20, 50, 20);

    }

    public void addParent(Item parent, int location){
        parents.put(parent, location);
    }

    public void addConnection(Item parent, int location, ConnectionTextMessage connectionTextMessage){
        Connection connection = new Connection(this, parent, connectionTextMessage);
        connections.put(connection, location);
    }

    public Connection getConnectionByParent(Item parent){
        if (connections.keySet().iterator().hasNext()){
            Connection con = connections.keySet().iterator().next();
            if (con.getParent() == parent)
                return con;
        }
        return null;
    }
}
