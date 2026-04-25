package com.animalartstudio.server.service

import com.animalartstudio.server.web.dto.HelpArticleDto

object HelpCatalog {
  val articles: List<HelpArticleDto> =
      listOf(
          HelpArticleDto(
              id = "start-drawing",
              title = "How a lesson works",
              body =
                  """
            Each step shows you what to add next. Your coach never grades you — it helps you
            play with lines until it looks right to you. When the coach says a step is complete,
            you move to the next part of the animal.
            """
                      .trimIndent(),
          ),
          HelpArticleDto(
              id = "troubleshoot-blank",
              title = "My drawing is not showing up",
              body =
                  """
            Make sure the canvas is in color (not the eraser) and that your screen brightness
            is comfortable. If nothing appears, try bigger, slower lines — the coach loves to see
            your marks!
            """
                      .trimIndent(),
          ),
          HelpArticleDto(
              id = "troubleshoot-sound",
              title = "I cannot hear the animal friends",
              body =
                  """
            Ask a grown-up to check the tablet volume, and the mute switch if your device has one.
            You can also enjoy the app quietly if sound needs to stay off.
            """
                      .trimIndent(),
          ),
          HelpArticleDto(
              id = "magic-unlock",
              title = "What is the wiggly magic surprise?",
              body =
                  """
            The longer you play with a step and use the coach’s gentle tips, the more practice
            stars you save up. When you have enough practice and finish the last step, your animal
            can wiggle, hop, or splash in a little celebration.
            """
                      .trimIndent(),
          ),
          HelpArticleDto(
              id = "parents-privacy",
              title = "Privacy & sharing",
              body =
                  """
            The app is built for play at home. Crash notes help the developer make things sturdier.
            You choose when to connect to the internet. Ask a parent before sharing your drawing
            outside the family.
            """
                      .trimIndent(),
          ),
      )
}
