package com.example.android.rogueai;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.ThreadLocalRandom;

/**
 * App created by Andy Osborne March 2017
 *
 *      This is an expansion to the fantastic deck building card game Star Realms by White Wizard
 * Games ( http://www.whitewizardgames.com/ ) that my son and I love to play.
 *      This expansion is similar to the co-op version called "Nemesis Beast" in which two or more
 * players cooperatively battle an enemy represented by a card. The card in this case is replaced
 * by the computer, called the Rogue A.I.
 *
 * Setup:
 * - Give Rogue A.I. the Brainworld card. This is its starting card.
 * - All other setup is the same as the standard game
 *
 * Rules:
 * - Any red card revealed either during setup or during play is placed in front of the Brainworld
 * and is now a part of the Rogue A.I.'s military.
 * - Rogue A.I.'s defense is equal to the Brainworld's defense.
 * - Rogue A.I.'s attack is equal to the combined attack of all cards in its possession.
 * - Whenever a player damages the Rogue A.I., that player must click the center message screen
 * and unveil a random event.
 * - Rogue A.I. can only lose cards by the random events displayed.
 *
 * Circuit board banners by Freepik ( www.freepik.com )
 */

public class MainActivity extends AppCompatActivity {

    /** Rogue A.I. elements */
    View mRogueView;
    View mRogueHealButton;
    TextView mRogueHealthTextView;
    private int mRogueHealth = 0;

    /** Player elements */
    View mPlayerView;
    View mPlayerHealButton;
    TextView mPlayerHealthTextView;
    private int mPlayerHealth = 0;

    /** Message View */
    private View mMessage;
    private TextView mMessageTextView;

    /** Used to set starting health for players */
    private static int STARTING_HEALTH = 100;

    /** Values used to heal and damage players */
    private static int STANDARD_DAMAGE = 1;
    private static int EXTRA_DAMAGE = 5;
    private static int STANDARD_HEAL = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set color of action bar text
        try {
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#35FF35'>Rogue A.I. </font>"));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        /* Setup center message event screen view */
        mMessageTextView = (TextView) findViewById(R.id.tv_message);
        Typeface terminalFont = Typeface.createFromAsset(getAssets(), "fonts/terminus.ttf");
        mMessageTextView.setTypeface(terminalFont);
        mMessage = findViewById(R.id.message_view);
        mMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMessageViewClicked();
            }
        });
        mMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mMessageTextView.setText("");
                return true;
            }
        });

        /* Setup enemy A.I. player */
        mRogueView = findViewById(R.id.view_rogue);
        mRogueHealthTextView = (TextView) findViewById(R.id.tv_rogue_health);
        mRogueHealth = STARTING_HEALTH;
        mRogueHealthTextView.setText(String.valueOf(mRogueHealth));
        mRogueHealButton = findViewById(R.id.view_rogue_heal);

        /* Setup player */
        mPlayerView = findViewById(R.id.view_player);
        mPlayerHealthTextView = (TextView) findViewById(R.id.tv_player_health);
        mPlayerHealth = STARTING_HEALTH;
        mPlayerHealthTextView.setText(String.valueOf(mPlayerHealth));
        mPlayerHealButton = findViewById(R.id.view_player_heal);

        // Rogue buttons //////////////////////////////////////////////////////////////////////////
        /* When user clicks the Rogue view, decrease Rogue health by 1 */
        mRogueView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Decrease health by 1
                changeRogueHealth(STANDARD_DAMAGE);

                // Animation
                Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.fade_in);
                mRogueView.startAnimation(animFadeIn);
            }
        });

        /* When user clicks on the Rogue heal button, increase health by 1 */
        mRogueHealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Increase health by 1
                changeRogueHealth(STANDARD_HEAL);

                // Animation
                Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.heal_button_fade);
                mRogueHealButton.startAnimation(animFadeIn);
            }
        });

        // Player buttons /////////////////////////////////////////////////////////////////////////
        mPlayerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Decrease health by 1
                changePlayerHealth(STANDARD_DAMAGE);

                // Animation
                Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.fade_in);
                mPlayerView.startAnimation(animFadeIn);
            }
        });

        mPlayerHealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Increase health by 1
                changePlayerHealth(STANDARD_HEAL);

                // Animation
                Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.heal_button_fade);
                mPlayerHealButton.startAnimation(animFadeIn);
            }
        });
    }

    /**
     * Change the player's health using the value passed to the method.
     *
     * Example (damage): to damage the player, pass in a positive value to indicate how much
     * damage should occur (i.e. passing a value of '5' will decrease the player's health
     * from 100 to 95).
     *
     * Example (heal): to heal or increase the player's health pass in a negative value (i.e.
     * passing a value of '-1' will increase the player's health from 100 to 101).
     *
     * @param damage that the player will receive
     */
    private void changePlayerHealth(int damage) {
        mPlayerHealth = mPlayerHealth - damage;

        // Health cannot be lower than 0
        if (mPlayerHealth <= 0) {
            mPlayerHealth = 0;
            mPlayerHealthTextView.setText(R.string.dead);
            // Set losing message for player
            mMessageTextView.setText(R.string.rogue_wins_message);

            // Disable message center clicks
            mMessage.setClickable(false);
        } else {
            mPlayerHealthTextView.setText(String.valueOf(mPlayerHealth));
        }
    }

    /**
     * Change the Rogue A.I.'s health using the value passed to the method.
     *
     * Example (damage): to damage the Rogue A.I., pass in a positive value to indicate how much
     * damage should occur (i.e. passing a value of '5' will decrease its health
     * from 100 to 95).
     *
     * Example (heal): to heal or increase the Rogue A.I.'s health pass in a negative value (i.e.
     * passing a value of '-1' will increase its health from 100 to 101).
     *
     * @param damage that the Rogue A.I. will receive
     */
    private void changeRogueHealth(int damage) {
        mRogueHealth = mRogueHealth - damage;

        // Health cannot be lower than 0
        if (mRogueHealth <= 0) {
            mRogueHealth = 0;
            mRogueHealthTextView.setText(R.string.dead);
            // Set winning message for player
            mMessageTextView.setText(R.string.player_wins_message);

            // Disable message center clicks
            mMessage.setClickable(false);
        } else {
            mRogueHealthTextView.setText(String.valueOf(mRogueHealth));
        }
    }

    /**
     * Reset each player's health and clear message event screen
     */
    public void onRestartButtonClicked() {
        // Reset player health
        mPlayerHealth = STARTING_HEALTH;
        mPlayerHealthTextView.setText(String.valueOf(mPlayerHealth));
        mRogueHealth = STARTING_HEALTH;
        mRogueHealthTextView.setText(String.valueOf(mRogueHealth));

        // Reset message center
        mMessageTextView.setText("");
        mMessage.setClickable(true);
    }

    /**
     * Generate a random event in the message center when the user clicks the message screen. Each
     * event will either hurt the Rogue A.I. or hurt the players.
     */
    private void onMessageViewClicked() {
        // Generate random number
        int randomNum = ThreadLocalRandom.current().nextInt(1, 6);

        // Use the random number to initiate an event
        switch (randomNum) {
            case 1:
                mMessageTextView.setText(R.string.event_one);
                break;
            case 2:
                mMessageTextView.setText(R.string.event_two);
                changePlayerHealth(EXTRA_DAMAGE);
                break;
            case 3:
                mMessageTextView.setText(R.string.event_three);
                changeRogueHealth(EXTRA_DAMAGE);
                break;
            case 4:
                mMessageTextView.setText(R.string.event_four);
                break;
            case 5:
                mMessageTextView.setText(R.string.event_five);
                break;
        }

        // Screen click animation
        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.heal_button_fade);
        mMessage.startAnimation(animFadeIn);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_restart:
                onRestartButtonClicked();
                return true;
        }
        return true;
    }
}
